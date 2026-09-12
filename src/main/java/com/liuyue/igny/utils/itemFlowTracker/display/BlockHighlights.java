package com.liuyue.igny.utils.itemFlowTracker.display;

import com.liuyue.igny.utils.display.DisplayIndex;
import com.liuyue.igny.utils.display.VirtualDisplay;
import com.liuyue.igny.utils.itemFlowTracker.core.Nesting;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

public final class BlockHighlights {
    private static final long PRUNE_TICKS = 200L;
    private static final DisplayIndex<BlockPos, HighlightEntry> DISPLAYS = new DisplayIndex<>();

    public static void tick(ServerLevel level) {
        for (TrackingWatch.PendingBlock pending : TrackingWatch.drainPending(level)) {
            if (level.isLoaded(pending.pos())) {
                apply(level, pending.pos(), pending.mark());
            }
        }

        sweep(level);
    }

    public static boolean active() {
        return DISPLAYS.active();
    }

    public static void clear() {
        DISPLAYS.forEach(HighlightEntry::dispose);
        DISPLAYS.clear();
    }

    private static void sweep(ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();
        Map<BlockPos, TrackingWatch.Watch> watches = TrackingWatch.blocks(dimension);
        long now = level.getGameTime();
        Iterator<Map.Entry<BlockPos, TrackingWatch.Watch>> it = watches.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<BlockPos, TrackingWatch.Watch> entry = it.next();
            TrackingWatch.Watch watch = entry.getValue();
            BlockPos pos = entry.getKey();

            if (!level.isLoaded(pos)) {
                watch.setMark(null);
                dispose(dimension, pos);
                continue;
            }

            TrackMark mark = markAt(level, pos, watch);

            if (mark != null) {
                watch.setMark(mark);
                apply(level, pos, mark);
                continue;
            }

            TrackMark lost = watch.mark;
            watch.setMark(null);

            if (lost != null) {
                TrackingWatch.rememberLoss(level, pos, lost);
            }

            dispose(dimension, pos);

            if (now - watch.lastTouched > PRUNE_TICKS) {
                it.remove();
            }
        }
    }

    private static void apply(ServerLevel level, BlockPos pos, TrackMark mark) {
        ResourceKey<Level> dimension = level.dimension();
        HighlightEntry tracked = DISPLAYS.get(dimension, pos);

        if (tracked != null && mark.equals(tracked.mark)) {
            return;
        }

        HighlightEntry fresh = new HighlightEntry();
        fresh.mark = mark;
        fresh.display = create(level, pos, mark);
        DISPLAYS.put(dimension, pos, fresh);

        if (tracked != null) {
            tracked.dispose();
        }
    }

    private static void dispose(ResourceKey<Level> dimension, BlockPos pos) {
        HighlightEntry entry = DISPLAYS.remove(dimension, pos);

        if (entry != null) {
            entry.dispose();
        }
    }

    @Nullable
    private static TrackMark markAt(ServerLevel level, BlockPos pos, TrackingWatch.Watch watch) {
        TrackMark mark = TrackingWatch.blockMarkOf(level, pos, watch);

        if (mark != null) {
            return mark;
        }

        if (level.getBlockEntity(pos) instanceof Container container) {
            return Nesting.inContainer(container);
        }

        return null;
    }

    @Nullable
    private static VirtualDisplay create(ServerLevel level, BlockPos pos, TrackMark mark) {
        VirtualDisplay display = VirtualDisplay.ofBlock(level, pos, level.getBlockState(pos));

        if (display == null) {
            return null;
        }

        display.glow(mark.rgb()).bright().sync();
        return display;
    }
}
