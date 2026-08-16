package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StandingSignBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final StandingSignBlockProtocolAdapter INSTANCE = new StandingSignBlockProtocolAdapter();

    public StandingSignBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int rotation = fromState.getValue(StandingSignBlock.ROTATION);
        return rotation & 0b0000_1111;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int rotation = extraProtocolValue & 0b0000_1111;
        return fromState.setValue(StandingSignBlock.ROTATION, rotation);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}