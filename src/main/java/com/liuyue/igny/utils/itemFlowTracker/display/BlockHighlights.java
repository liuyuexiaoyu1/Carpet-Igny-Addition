package com.liuyue.igny.utils.itemFlowTracker.display;

import com.liuyue.igny.utils.itemFlowTracker.core.Nesting;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
//#if MC >= 12003
import net.minecraft.world.level.block.DecoratedPotBlock;
//#endif
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
                watch.mark = null;
                dispose(dimension, pos);
                continue;
            }

            TrackMark mark = markAt(level, pos, watch);

            if (mark != null) {
                watch.mark = mark;
                apply(level, pos, mark);
                continue;
            }

            watch.mark = null;
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

        if (tracked != null) {
            tracked.dispose();
        }

        HighlightEntry fresh = new HighlightEntry();
        fresh.mark = mark;
        fresh.display = create(level, pos, mark);
        DISPLAYS.put(dimension, pos, fresh);
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
        BlockState state = level.getBlockState(pos);
        VirtualDisplay display = usesSpecialRenderer(state)
                ? createItem(level, pos, state)
                : createBlock(level, pos, state);

        if (display == null) {
            return null;
        }

        display.glow(mark).bright().sync();
        return display;
    }

    private static VirtualDisplay createBlock(ServerLevel level, BlockPos pos, BlockState state) {
        return VirtualDisplay
                .block(level, pos.getX(), pos.getY(), pos.getZ(), state)
                .transform(Shapes.outline());
    }

    @Nullable
    private static VirtualDisplay createItem(ServerLevel level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(state.getBlock());

        if (stack.isEmpty()) {
            return null;
        }

        return VirtualDisplay
                .item(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack)
                .transform(Shapes.item(state));
    }

    private static boolean usesSpecialRenderer(BlockState state) {
        //#if MC >= 12003
        if (state.getBlock() instanceof DecoratedPotBlock) {
            return true;
        }
        //#endif
        return state.getBlock() instanceof AbstractChestBlock<?> || state.getBlock() instanceof ShulkerBoxBlock;
    }
}
