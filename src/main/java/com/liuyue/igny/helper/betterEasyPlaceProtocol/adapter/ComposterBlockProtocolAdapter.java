package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComposterBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final ComposterBlockProtocolAdapter INSTANCE = new ComposterBlockProtocolAdapter();

    public ComposterBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        return fromState.getValue(ComposterBlock.LEVEL) & 0b0000_1111;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        if (!IGNYSettings.AIR_COMPOST.value()) return fromState;
        int level = Math.min(extraProtocolValue & 0b0000_1111, ComposterBlock.MAX_LEVEL);
        return fromState.setValue(ComposterBlock.LEVEL, level);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}