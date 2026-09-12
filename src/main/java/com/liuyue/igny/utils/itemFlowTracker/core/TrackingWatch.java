package com.liuyue.igny.utils.itemFlowTracker.core;

import com.liuyue.igny.utils.itemFlowTracker.ItemFlowTrackerSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class TrackingWatch {
    private static final Map<ResourceKey<Level>, Map<BlockPos, Watch>> BLOCKS = new HashMap<>();
    private static final Map<ResourceKey<Level>, Map<Integer, Watch>> ENTITIES = new HashMap<>();
    private static final List<PendingBlock> PENDING = new ArrayList<>();

    public static final class Watch {
        @Nullable
        public TrackMark mark;

        @Nullable
        public TrackMark blockMark;

        @Nullable
        public Block blockMarkOwner;

        @Nullable
        public Vec3 trailLast;

        public long lastTouched;
    }

    public record PendingBlock(ServerLevel level, BlockPos pos, TrackMark mark) {
    }

    public static Map<BlockPos, Watch> blocks(ResourceKey<Level> dimension) {
        return BLOCKS.computeIfAbsent(dimension, key -> new HashMap<>());
    }

    public static Map<Integer, Watch> entities(ResourceKey<Level> dimension) {
        return ENTITIES.computeIfAbsent(dimension, key -> new HashMap<>());
    }

    public static List<PendingBlock> drainPending(ServerLevel level) {
        List<PendingBlock> drained = new ArrayList<>();
        Iterator<PendingBlock> it = PENDING.iterator();

        while (it.hasNext()) {
            PendingBlock pending = it.next();

            if (pending.level() == level) {
                drained.add(pending);
                it.remove();
            }
        }

        return drained;
    }

    public static boolean pending() {
        return !PENDING.isEmpty();
    }

    public static void onEnterContainer(@Nullable Container container) {
        onEnterContainer(container, null);
    }

    public static void onEnterContainer(@Nullable Container container, @Nullable Vec3 from) {
        if (!ItemFlowTrackerSettings.enabled()) {
            return;
        }

        Containers.forEachLeaf(container, leaf -> {
            TrackMark mark = Nesting.inContainer(leaf);

            if (mark == null) {
                return;
            }

            if (leaf instanceof BlockEntity blockEntity) {
                if (blockEntity.getLevel() instanceof ServerLevel level) {
                    watchBlock(level, blockEntity.getBlockPos(), from, mark);
                }
            } else if (leaf instanceof Entity entity && !entity.level().isClientSide()) {
                watchEntity(entity, from);
            }
        });
    }

    @Nullable
    public static Vec3 positionOf(@Nullable Container container) {
        Vec3[] found = new Vec3[1];

        Containers.forEachLeaf(container, leaf -> {
            if (found[0] != null) {
                return;
            }

            if (leaf instanceof BlockEntity blockEntity) {
                found[0] = Vec3.atCenterOf(blockEntity.getBlockPos());
            } else if (leaf instanceof Entity entity) {
                found[0] = entity.position();
            }
        });

        return found[0];
    }

    public static void watchBlock(ServerLevel level, BlockPos pos) {
        watchBlock(level, pos, null);
    }

    public static void watchBlock(ServerLevel level, BlockPos pos, @Nullable Vec3 from) {
        TrackMark mark = null;

        if (level.getBlockEntity(pos) instanceof Container container) {
            mark = Nesting.inContainer(container);
        }

        watchBlock(level, pos, from, mark);
    }

    public static void watchBlock(ServerLevel level, BlockPos pos, @Nullable Vec3 from, @Nullable TrackMark mark) {
        if (!ItemFlowTrackerSettings.enabled()) {
            return;
        }

        BlockPos anchor = pos.immutable();
        Watch watch = blocks(level.dimension()).computeIfAbsent(anchor, key -> new Watch());
        seed(watch, from);
        watch.lastTouched = level.getGameTime();

        if (mark != null) {
            PENDING.add(new PendingBlock(level, anchor, mark));
        }
    }

    public static void watchEntity(Entity entity) {
        watchEntity(entity, null);
    }

    public static void watchEntity(Entity entity, @Nullable Vec3 from) {
        if (!ItemFlowTrackerSettings.enabled()) {
            return;
        }

        seed(entities(entity.level().dimension()).computeIfAbsent(entity.getId(), key -> new Watch()), from);
    }

    private static void seed(Watch watch, @Nullable Vec3 from) {
        if (watch.trailLast == null) {
            watch.trailLast = from;
        }
    }

    public static void markBlock(ServerLevel level, BlockPos pos, TrackMark mark) {
        BlockPos anchor = pos.immutable();
        Watch watch = blocks(level.dimension()).computeIfAbsent(anchor, key -> new Watch());
        watch.blockMark = mark;
        watch.blockMarkOwner = level.getBlockState(pos).getBlock();
        watch.lastTouched = level.getGameTime();
        PENDING.add(new PendingBlock(level, anchor, mark));
    }

    public static void takeBlockMark(Level level, BlockPos pos, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel) || stack.isEmpty()) {
            return;
        }

        Watch watch = blocks(serverLevel.dimension()).get(pos);

        if (watch == null || !Tracking.isLive(watch.blockMark) || watch.blockMarkOwner == null) {
            return;
        }

        if (stack.getItem() == watch.blockMarkOwner.asItem()) {
            Tracking.setIfAbsent(stack, watch.blockMark);
        }
    }

    @Nullable
    public static TrackMark peekBlockMark(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        Watch watch = blocks(serverLevel.dimension()).get(pos);
        return watch != null && Tracking.isLive(watch.blockMark) ? watch.blockMark : null;
    }

    public static void clearBlockMark(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        Watch watch = blocks(serverLevel.dimension()).get(pos);

        if (watch != null) {
            watch.blockMark = null;
            watch.blockMarkOwner = null;
        }
    }

    @Nullable
    public static TrackMark blockMarkOf(ServerLevel level, BlockPos pos, Watch watch) {
        if (watch.blockMark == null) {
            return null;
        }

        if (!Tracking.isLive(watch.blockMark) || level.getBlockState(pos).getBlock() != watch.blockMarkOwner) {
            watch.blockMark = null;
            watch.blockMarkOwner = null;
            return null;
        }

        watch.lastTouched = level.getGameTime();
        return watch.blockMark;
    }

    public static void clearAll() {
        BLOCKS.clear();
        ENTITIES.clear();
        PENDING.clear();
    }
}
