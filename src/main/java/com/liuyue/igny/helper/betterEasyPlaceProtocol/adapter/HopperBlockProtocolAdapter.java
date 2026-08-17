package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HopperBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final HopperBlockProtocolAdapter INSTANCE = new HopperBlockProtocolAdapter();

    public HopperBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        boolean isEnabled = fromState.getValue(HopperBlock.ENABLED);
        return isEnabled ? 0b0001 : 0b0000;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isEnabled = (extraProtocolValue & 0b0001) == 0b0001;
        return fromState.setValue(HopperBlock.ENABLED, isEnabled);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}