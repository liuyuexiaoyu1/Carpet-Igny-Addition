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
    private static final double BRIDGE_RANGE = 3.0D;
    private static final long LOSS_TICKS = 3L;
    private static final Map<ResourceKey<Level>, Map<BlockPos, Watch>> BLOCKS = new HashMap<>();
    private static final Map<ResourceKey<Level>, Map<Integer, Watch>> ENTITIES = new HashMap<>();
    private static final Map<ResourceKey<Level>, List<Loss>> LOSSES = new HashMap<>();
    private static final List<PendingBlock> PENDING = new ArrayList<>();
    private static final List<TrailHop> PENDING_TRAILS = new ArrayList<>();

    private static boolean handDrop;

    @Nullable
    private static Vec3 ejection;

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

        public void setMark(@Nullable TrackMark mark) {
            if (this.mark != null && this.mark != mark) {
                this.trailLast = null;
            }

            this.mark = mark;
        }
    }

    public record Loss(BlockPos pos, TrackMark mark, long time) {
    }

    public record PendingBlock(ServerLevel level, BlockPos pos, TrackMark mark) {
    }

    public record TrailHop(ServerLevel level, TrackMark mark, @Nullable Vec3 previous, @Nullable Vec3 from, Vec3 to) {
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

    public static List<TrailHop> drainTrailHops(ServerLevel level) {
        List<TrailHop> drained = new ArrayList<>();
        Iterator<TrailHop> it = PENDING_TRAILS.iterator();

        while (it.hasNext()) {
            TrailHop hop = it.next();

            if (hop.level() == level) {
                drained.add(hop);
                it.remove();
            }
        }

        return drained;
    }

    public static boolean pending() {
        return !PENDING.isEmpty() || !PENDING_TRAILS.isEmpty();
    }

    public static void beginHandDrop() {
        handDrop = true;
    }

    public static void endHandDrop() {
        handDrop = false;
    }

    public static boolean takeHandDrop() {
        boolean taken = handDrop;
        handDrop = false;
        return taken;
    }

    public static void beginEjection(BlockPos pos) {
        ejection = Vec3.atCenterOf(pos);
    }

    public static void endEjection() {
        ejection = null;
    }

    @Nullable
    public static Vec3 takeEjection() {
        Vec3 taken = ejection;
        ejection = null;
        return taken;
    }

    @Nullable
    public static Vec3 lostAnchor(ServerLevel level, TrackMark mark, Vec3 near) {
        List<Loss> losses = LOSSES.get(level.dimension());

        if (losses == null) {
            return null;
        }

        long now = level.getGameTime();
        Vec3 best = null;
        double bestDistance = BRIDGE_RANGE;

        for (Loss loss : losses) {
            if (loss.mark() != mark || now - loss.time() > LOSS_TICKS) {
                continue;
            }

            Vec3 centre = Vec3.atCenterOf(loss.pos());
            double distance = centre.distanceTo(near);

            if (distance <= bestDistance) {
                best = centre;
                bestDistance = distance;
            }
        }

        return best;
    }

    public static void rememberLoss(ServerLevel level, BlockPos pos, TrackMark mark) {
        List<Loss> losses = LOSSES.computeIfAbsent(level.dimension(), key -> new ArrayList<>());
        long now = level.getGameTime();
        losses.removeIf(loss -> now - loss.time() > LOSS_TICKS);
        losses.add(new Loss(pos.immutable(), mark, now));
    }

    public static void onEnterContainer(@Nullable Container container, @Nullable Vec3 from, @Nullable Vec3 previous, @Nullable TrackMark moved) {
        if (!ItemFlowTrackerSettings.enabled()) {
            return;
        }

        if (moved != null) {
            watch(holderOf(container, moved), from, previous, moved);
            return;
        }

        Containers.forEachLeaf(container, leaf -> watch(leaf, from, previous, Nesting.inContainer(leaf)));
    }

    private static void watch(@Nullable Container leaf, @Nullable Vec3 from, @Nullable Vec3 previous, @Nullable TrackMark mark) {
        if (leaf == null || mark == null) {
            return;
        }

        if (leaf instanceof BlockEntity blockEntity) {
            if (blockEntity.getLevel() instanceof ServerLevel level) {
                watchBlock(level, blockEntity.getBlockPos(), from, previous, mark);
            }
        } else if (leaf instanceof Entity entity && !entity.level().isClientSide()) {
            watchEntity(entity, from);
        }
    }

    @Nullable
    public static Container holderOf(@Nullable Container container, TrackMark mark) {
        List<Container> leaves = Containers.leaves(container);

        for (Container leaf : leaves) {
            if (Nesting.holds(leaf, mark)) {
                return leaf;
            }
        }

        return leaves.isEmpty() ? null : leaves.get(0);
    }

    @Nullable
    public static Vec3 positionOf(@Nullable Container leaf) {
        if (leaf instanceof BlockEntity blockEntity) {
            return Vec3.atCenterOf(blockEntity.getBlockPos());
        }

        return leaf instanceof Entity entity ? entity.position() : null;
    }

    @Nullable
    public static Vec3 trailOf(@Nullable Container leaf) {
        return leaf instanceof Entity entity ? trailOf(entity) : null;
    }

    @Nullable
    public static Vec3 trailOf(Entity entity) {
        Watch watch = entities(entity.level().dimension()).get(entity.getId());
        return watch != null ? watch.trailLast : null;
    }

    public static void watchBlock(ServerLevel level, BlockPos pos) {
        TrackMark mark = null;

        if (level.getBlockEntity(pos) instanceof Container container) {
            mark = Nesting.inContainer(container);
        }

        watchBlock(level, pos, null, null, mark);
    }

    public static void watchBlock(ServerLevel level, BlockPos pos, @Nullable Vec3 from, @Nullable Vec3 previous, @Nullable TrackMark mark) {
        if (!ItemFlowTrackerSettings.enabled()) {
            return;
        }

        BlockPos anchor = pos.immutable();
        Watch watch = blocks(level.dimension()).computeIfAbsent(anchor, key -> new Watch());
        seed(watch, from);
        watch.lastTouched = level.getGameTime();

        if (mark != null) {
            PENDING.add(new PendingBlock(level, anchor, mark));
            PENDING_TRAILS.add(new TrailHop(level, mark, previous, from, Vec3.atCenterOf(anchor)));
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
        LOSSES.clear();
        PENDING.clear();
        PENDING_TRAILS.clear();
    }
}
