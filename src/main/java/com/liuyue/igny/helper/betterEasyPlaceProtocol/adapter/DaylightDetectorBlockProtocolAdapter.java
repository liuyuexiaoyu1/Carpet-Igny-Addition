package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DaylightDetectorBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final DaylightDetectorBlockProtocolAdapter INSTANCE = new DaylightDetectorBlockProtocolAdapter();

    public DaylightDetectorBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        boolean isInverted = fromState.getValue(DaylightDetectorBlock.INVERTED);
        return isInverted ? 0b0001 : 0b0000;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isInverted = (extraProtocolValue & 0b0001) == 0b0001;
        return fromState.setValue(DaylightDetectorBlock.INVERTED, isInverted);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}