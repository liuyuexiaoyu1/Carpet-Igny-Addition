package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HorizontalDirectionalBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final HorizontalDirectionalBlockProtocolAdapter INSTANCE = new HorizontalDirectionalBlockProtocolAdapter();

    public HorizontalDirectionalBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        Direction facing = fromState.getValue(HorizontalDirectionalBlock.FACING);
        return switch (facing) {
            case NORTH -> 0b0000;
            case EAST -> 0b0001;
            case SOUTH -> 0b0010;
            case WEST -> 0b0011;
            default -> 0b0000;
        };
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int directionIndex = extraProtocolValue & 0b0011;

        Direction facing = switch (directionIndex) {
            case 0 -> Direction.NORTH;
            case 1 -> Direction.EAST;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.WEST;
            default -> Direction.NORTH;
        };

        return fromState.setValue(HorizontalDirectionalBlock.FACING, facing);
    }

    @Override
    public @NotNull BlockProtocolStateAdapter.ProtocolType igny$getProtocolType() {
        return BlockProtocolStateAdapter.ProtocolType.REPLACE;
    }
}