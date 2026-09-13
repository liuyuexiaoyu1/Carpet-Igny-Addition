package com.liuyue.igny.utils;

import com.liuyue.igny.mixins.rule.itemFlowTracker.accessors.CompoundContainerAccessor;
import com.liuyue.igny.mixins.rule.itemFlowTracker.compat.tis.LargeBarrelHelperInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ContainerUtil {

    public static List<Container> leaves(@Nullable Container container) {
        List<Container> leaves = new ArrayList<>();
        forEachLeaf(container, leaves::add);
        return leaves;
    }

    public static void forEachLeaf(@Nullable Container container, Consumer<Container> sink) {
        if (container == null) {
            return;
        }

        if (container instanceof CompoundContainer compound) {
            CompoundContainerAccessor accessor = (CompoundContainerAccessor) compound;
            forEachLeaf(accessor.igny$container1(), sink);
            forEachLeaf(accessor.igny$container2(), sink);
            return;
        }

        sink.accept(container);
    }

    @Nullable
    public static BlockPos partner(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.hasProperty(ChestBlock.TYPE) || !state.hasProperty(ChestBlock.FACING)) {
            return largeBarrelPartner(level, pos, state);
        }

        ChestType type = state.getValue(ChestBlock.TYPE);

        if (type == ChestType.SINGLE) {
            return null;
        }

        Direction facing = state.getValue(ChestBlock.FACING);
        BlockPos other = pos.relative(type == ChestType.LEFT ? facing.getClockWise() : facing.getCounterClockWise());

        if (!level.isLoaded(other) || !level.getBlockState(other).hasProperty(ChestBlock.TYPE)) {
            return null;
        }

        return other;
    }
    @Nullable
    private static BlockPos largeBarrelPartner(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof BarrelBlock)) {
            return null;
        }

        if (!Boolean.TRUE.equals(RuleUtil.getCarpetRulesValue("carpet-tis-addition", "largeBarrel"))) {
            return null;
        }

        if (!LargeBarrelHelperInvoker.igny$isLargeBarrel(state, level, pos)) {
            return null;
        }

        BlockPos other = LargeBarrelHelperInvoker.igny$getOtherPos(state, level, pos);

        if (other == null || !level.isLoaded(other) || !(level.getBlockState(other).getBlock() instanceof BarrelBlock)) {
            return null;
        }

        return other;
    }

    @Nullable
    public static BlockPos blockPosOf(@Nullable Container container) {
        return container instanceof BlockEntity blockEntity ? blockEntity.getBlockPos() : null;
    }

    @Nullable
    public static Vec3 centreOf(@Nullable Container container) {
        if (container instanceof BlockEntity blockEntity) {
            BlockPos pos = blockEntity.getBlockPos();
            Level level = blockEntity.getLevel();
            BlockPos other = level == null ? null : partner(level, pos);

            if (other == null) {
                return Vec3.atCenterOf(pos);
            }

            return Vec3.atCenterOf(pos).add(Vec3.atCenterOf(other)).scale(0.5D);
        }

        return container instanceof Entity entity ? entity.position() : null;
    }
}
