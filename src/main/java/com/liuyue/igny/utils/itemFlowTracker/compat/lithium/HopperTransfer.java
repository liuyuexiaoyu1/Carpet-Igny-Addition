package com.liuyue.igny.utils.itemFlowTracker.compat.lithium;

import com.liuyue.igny.utils.ContainerUtil;
import com.liuyue.igny.utils.itemFlowTracker.core.Nesting;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class HopperTransfer {

    public record Source(Vec3 at, @Nullable Vec3 previous, @Nullable BlockPos pos) {
    }

    public record Move(TrackMark mark, @Nullable Source source, Container target, ItemStack stack, int before, int[] targetBefore) {
    }

    @Nullable
    public static Move prepare(Container target, @Nullable Direction direction, ItemStack stack) {
        Level level = levelOf(target);

        if (level == null) {
            return null;
        }

        TrackMark mark = Nesting.inStack(stack);
        Source source = sourceOf(target, direction, mark, stack);

        if (mark == null) {
            if (source == null || source.pos() == null) {
                return null;
            }

            mark = TrackingWatch.peekBlockMark(level, source.pos());

            if (mark == null) {
                return null;
            }
        }

        return new Move(mark, source, target, stack, stack.getCount(), snapshot(target));
    }

    private static int[] snapshot(Container target) {
        int[] counts = new int[target.getContainerSize()];

        for (int slot = 0; slot < counts.length; slot++) {
            counts[slot] = target.getItem(slot).getCount();
        }

        return counts;
    }

    public static void apply(Move move, boolean moved) {
        int movedAmount = Math.max(move.before() - move.stack().getCount(), 0);
        int amount = moved ? movedAmount : move.before();

        if (amount > 0) {
            Tracking.refunded(move.stack(), amount);
        }

        if (!moved) {
            return;
        }

        Tracking.withdrawn(move.stack(), movedAmount(move));
        attach(move);
        handOver(move);

        Source source = move.source();
        TrackingWatch.onEnterContainer(
                move.target(),
                source == null ? null : source.at(),
                source == null ? null : source.previous(),
                move.mark());

        if (source != null && source.pos() == null && source.at() != null) {
            TrackingWatch.link(source.at(), move.target(), move.mark());
        }
    }

    private static void handOver(Move move) {
        Source source = move.source();

        if (source == null || source.pos() == null) {
            return;
        }

        Level level = levelOf(move.target());

        if (level != null) {
            TrackingWatch.clearBlockMark(level, source.pos());
        }
    }

    private static void attach(Move move) {
        Container target = move.target();

        int size = Math.min(target.getContainerSize(), move.targetBefore().length);
        int gained = -1;
        boolean ambiguous = false;

        for (int slot = 0; slot < size; slot++) {
            ItemStack now = target.getItem(slot);

            if (now.isEmpty() || now.getCount() <= move.targetBefore()[slot]) {
                continue;
            }

            if (ItemStack.isSameItemSameComponents(now, move.stack())) {
                Tracking.setIfAbsent(now, move.mark());
                Tracking.refunded(now, movedAmount(move));
                return;
            }

            if (gained < 0) {
                gained = slot;
            } else {
                ambiguous = true;
            }
        }

        if (gained >= 0 && !ambiguous) {
            ItemStack now = target.getItem(gained);
            Tracking.setIfAbsent(now, move.mark());
            Tracking.refunded(now, movedAmount(move));
        }
    }

    private static int movedAmount(Move move) {
        return Math.max(move.before() - move.stack().getCount(), 0);
    }

    @Nullable
    private static Source sourceOf(Container target, @Nullable Direction direction, @Nullable TrackMark mark, ItemStack stack) {
        Level level = levelOf(target);

        if (level == null) {
            return null;
        }

        Source exact = mark == null ? null : exact(level, mark, stack);

        if (exact != null) {
            return exact;
        }

        for (BlockPos pos : positionsOf(target)) {
            BlockPos sourcePos = target instanceof Hopper
                    ? pos.above()
                    : direction == null ? null : pos.relative(direction.getOpposite());

            if (sourcePos == null) {
                continue;
            }

            Source container = container(level, sourcePos);

            if (container != null) {
                return container;
            }

            Source item = item(level, sourcePos, stack);

            if (item != null) {
                return item;
            }
        }

        BlockPos near = posOf(target);

        if (near == null || mark == null) {
            return null;
        }

        Vec3 carrier = TrackingWatch.lastCarrier((ServerLevel) level, mark, near);

        return carrier == null ? null : new Source(carrier, null, null);
    }

    private static List<BlockPos> positionsOf(Container target) {
        List<BlockPos> positions = new ArrayList<>(2);

        for (Container leaf : ContainerUtil.leaves(target)) {
            BlockPos pos = posOf(leaf);

            if (pos != null && !positions.contains(pos)) {
                positions.add(pos);
            }
        }

        return positions;
    }

    @Nullable
    private static Source exact(Level level, TrackMark mark, ItemStack stack) {
        for (Map.Entry<BlockPos, TrackingWatch.Watch> entry : TrackingWatch.blocks(level.dimension()).entrySet()) {
            if (entry.getValue().mark != mark) {
                continue;
            }

            Container container = HopperBlockEntity.getContainerAt(level, entry.getKey());

            for (Container leaf : ContainerUtil.leaves(container)) {
                if (holds(leaf, stack)) {
                    return new Source(ContainerUtil.centreOf(leaf), null, ContainerUtil.blockPosOf(leaf));
                }
            }
        }

        for (Map.Entry<Integer, TrackingWatch.Watch> entry : TrackingWatch.entities(level.dimension()).entrySet()) {
            if (entry.getValue().mark != mark) {
                continue;
            }

            Entity entity = level.getEntity(entry.getKey());

            if (entity instanceof ItemEntity item && item.getItem() == stack) {
                return new Source(item.position(), TrackingWatch.trailOf(item), null);
            }

            if (entity instanceof Container container && holds(container, stack)) {
                return new Source(entity.position(), TrackingWatch.trailOf(entity), null);
            }
        }

        return null;
    }

    @Nullable
    private static Source container(Level level, BlockPos pos) {
        Container container = HopperBlockEntity.getContainerAt(level, pos);

        if (container == null) {
            return null;
        }

        for (Container leaf : ContainerUtil.leaves(container)) {
            if (leaf instanceof BlockEntity || leaf instanceof Entity) {
                return new Source(TrackingWatch.positionOf(leaf), TrackingWatch.trailOf(leaf), ContainerUtil.blockPosOf(leaf));
            }
        }

        return null;
    }

    @Nullable
    private static Source item(Level level, BlockPos pos, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        Vec3 centre = Vec3.atCenterOf(pos);
        AABB box = new AABB(
                centre.x - 1.5D, centre.y - 1.5D, centre.z - 1.5D,
                centre.x + 1.5D, centre.y + 1.5D, centre.z + 1.5D);

        for (ItemEntity item : serverLevel.getEntitiesOfClass(ItemEntity.class, box)) {
            if (item.getItem() == stack) {
                return new Source(item.position(), TrackingWatch.trailOf(item), null);
            }
        }

        return null;
    }

    private static boolean holds(Container container, ItemStack stack) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (container.getItem(slot) == stack) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    private static Level levelOf(Container target) {
        if (target instanceof BlockEntity blockEntity) {
            return blockEntity.getLevel();
        }

        if (target instanceof Entity entity) {
            return entity.level();
        }

        for (Container leaf : ContainerUtil.leaves(target)) {
            if (leaf == target) {
                continue;
            }

            Level level = levelOf(leaf);

            if (level != null) {
                return level;
            }
        }

        return null;
    }

    @Nullable
    private static BlockPos posOf(Container target) {
        if (target instanceof BlockEntity blockEntity) {
            return blockEntity.getBlockPos();
        }

        if (target instanceof Entity entity) {
            return entity.blockPosition();
        }

        for (Container leaf : ContainerUtil.leaves(target)) {
            if (leaf == target) {
                continue;
            }

            BlockPos pos = posOf(leaf);

            if (pos != null) {
                return pos;
            }
        }

        return null;
    }
}
