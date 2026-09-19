package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
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
        boolean isOpen = fromState.getValue(FenceGateBlock.OPEN);
        boolean isPowered = fromState.getValue(FenceGateBlock.POWERED);
        boolean isInWall = fromState.getValue(FenceGateBlock.IN_WALL);

        int bits = 0;
        if (isOpen) bits |= 0b0001;
        if (isPowered) bits |= 0b0010;
        if (isInWall) bits |= 0b0100;

        return bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isOpen = (extraProtocolValue & 0b0001) == 0b0001;
        boolean isPowered = (extraProtocolValue & 0b0010) == 0b0010;
        boolean isInWall = (extraProtocolValue & 0b0100) == 0b0100;

        return fromState.setValue(FenceGateBlock.OPEN, isOpen)
                .setValue(FenceGateBlock.POWERED, isPowered)
                .setValue(FenceGateBlock.IN_WALL, isInWall);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}