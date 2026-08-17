package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FenceGateBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final FenceGateBlockProtocolAdapter INSTANCE = new FenceGateBlockProtocolAdapter();

    public FenceGateBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        Direction facing = fromState.getValue(FenceGateBlock.FACING);
        int facingIndex = switch (facing) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
        boolean isOpen = fromState.getValue(FenceGateBlock.OPEN);
        int bits = (facingIndex & 0b0011);
        if (isOpen) bits |= 0b0001_0000;

        return bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int facingIndex = extraProtocolValue & 0b0011;
        Direction facing = switch (facingIndex) {
            case 0 -> Direction.NORTH;
            case 1 -> Direction.EAST;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.WEST;
            default -> Direction.NORTH;
        };
        boolean isOpen = (extraProtocolValue & 0b0001_0000) == 0b0001_0000;

        return fromState.setValue(FenceGateBlock.FACING, facing)
                .setValue(FenceGateBlock.OPEN, isOpen)
                .setValue(FenceGateBlock.POWERED, isOpen);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}