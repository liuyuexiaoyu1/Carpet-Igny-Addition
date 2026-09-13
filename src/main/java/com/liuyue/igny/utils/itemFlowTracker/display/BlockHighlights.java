package com.liuyue.igny.utils.itemFlowTracker.display;

import com.liuyue.igny.utils.ContainerUtil;
import com.liuyue.igny.utils.display.DisplayIndex;
import com.liuyue.igny.utils.display.VirtualDisplay;
import com.liuyue.igny.utils.itemFlowTracker.core.Nesting;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

public final class BlockHighlights {
    private static final long PRUNE_TICKS = 200L;
    private static final float WIDE_WIDTH = 2.0F;
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
                dispose(level, pos, watch.mark);

                if (!Tracking.isLive(watch.mark) && !Tracking.isLive(watch.blockMark)) {
                    it.remove();
                }

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

            dispose(level, pos, lost);

            if (now - watch.lastTouched > PRUNE_TICKS) {
                it.remove();
            }
        }
    }

    private static void apply(ServerLevel level, BlockPos pos, TrackMark mark) {
        ResourceKey<Level> dimension = level.dimension();
        TrackingWatch.Watch watch = TrackingWatch.blocks(dimension).get(pos);
        BlockPos anchor = anchorOf(level, pos);
        HighlightEntry tracked = DISPLAYS.get(dimension, anchor);

        if (tracked != null) {
            if (mark.equals(tracked.mark) || stillHolds(level, anchor, tracked.mark)) {
                tracked.sync();
                move(dimension, watch, anchor);
                return;
            }
        }

        HighlightEntry fresh = new HighlightEntry();
        fresh.mark = mark;

        BlockPos other = ContainerUtil.partner(level, anchor);

        if (other == null) {
            highlight(fresh, level, anchor, mark);
        } else {
            wide(fresh, level, anchor, other, mark);
        }

        DISPLAYS.put(dimension, anchor, fresh);

        if (tracked != null) {
            tracked.dispose();
        }

        move(dimension, watch, anchor);
    }

    private static boolean stillHolds(ServerLevel level, BlockPos anchor, @Nullable TrackMark mark) {
        Container container = containerAt(level, anchor);
        return container != null && mark != null && Nesting.holds(container, mark);
    }

    private static void move(ResourceKey<Level> dimension, @Nullable TrackingWatch.Watch watch, BlockPos anchor) {
        if (watch == null) {
            return;
        }

        BlockPos previous = watch.displayAnchor;
        watch.displayAnchor = anchor;

        if (previous != null && !previous.equals(anchor)) {
            remove(dimension, previous);
        }
    }

    private static void highlight(HighlightEntry entry, ServerLevel level, BlockPos pos, TrackMark mark) {
        add(entry, VirtualDisplay.ofBlock(level, pos, level.getBlockState(pos)), mark);
    }

    private static void wide(HighlightEntry entry, ServerLevel level, BlockPos anchor, BlockPos other, TrackMark mark) {
        Vec3 seam = Vec3.atCenterOf(anchor).add(Vec3.atCenterOf(other)).scale(0.5D);
        add(entry, VirtualDisplay.ofBlock(level, seam, anchor, level.getBlockState(anchor), WIDE_WIDTH), mark);
    }

    private static void add(HighlightEntry entry, @Nullable VirtualDisplay display, TrackMark mark) {
        if (display == null) {
            return;
        }

        entry.displays.add(display.glow(mark.rgb()).bright());
        display.sync();
    }

    private static void dispose(ServerLevel level, BlockPos pos, @Nullable TrackMark mark) {
        ResourceKey<Level> dimension = level.dimension();
        TrackingWatch.Watch watch = TrackingWatch.blocks(dimension).get(pos);
        BlockPos anchor = watch != null && watch.displayAnchor != null ? watch.displayAnchor : anchorOf(level, pos);
        HighlightEntry entry = DISPLAYS.get(dimension, anchor);

        if (entry == null) {
            if (watch != null) {
                watch.displayAnchor = null;
            }

            return;
        }

        if (mark != null && !mark.equals(entry.mark)) {
            return;
        }

        if (watch != null) {
            watch.displayAnchor = null;
        }

        DISPLAYS.remove(dimension, anchor);
        entry.dispose();
    }

    private static void remove(ResourceKey<Level> dimension, BlockPos anchor) {
        HighlightEntry entry = DISPLAYS.remove(dimension, anchor);

        if (entry != null) {
            entry.dispose();
        }
    }

    private static BlockPos anchorOf(ServerLevel level, BlockPos pos) {
        BlockPos other = ContainerUtil.partner(level, pos);

        if (other == null) {
            return pos;
        }

        return pos.compareTo(other) <= 0 ? pos : other;
    }

    @Nullable
    private static TrackMark markAt(ServerLevel level, BlockPos pos, TrackingWatch.Watch watch) {
        TrackMark mark = TrackingWatch.blockMarkOf(level, pos, watch);

        if (mark != null) {
            return mark;
        }

        Container container = containerAt(level, pos);
        return container != null ? Nesting.inContainer(container) : null;
    }

    @Nullable
    private static Container containerAt(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof Container container)) {
            return null;
        }

        BlockPos other = ContainerUtil.partner(level, pos);

        if (other == null || !(level.getBlockEntity(other) instanceof Container sibling)) {
            return container;
        }

        return new CompoundContainer(container, sibling);
    }
}
