package com.liuyue.igny.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpectatorContainerTarget {
    public static boolean isContainerTarget(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return state.getMenuProvider(level, pos) != null || blockEntity instanceof Container;
    }
}
