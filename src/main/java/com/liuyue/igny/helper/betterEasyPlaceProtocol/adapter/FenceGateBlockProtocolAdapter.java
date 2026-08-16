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
        int bits = (isOpen ? 0b0001_0000 : 0b0000_0000);
        return protocolValue | bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isOpen = (extraProtocolValue & 0b0001_0000) == 0b0001_0000;
        return fromState.setValue(FenceGateBlock.OPEN, isOpen);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}