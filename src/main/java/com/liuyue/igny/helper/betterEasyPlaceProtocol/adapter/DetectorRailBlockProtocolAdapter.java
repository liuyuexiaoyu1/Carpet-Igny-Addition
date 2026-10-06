package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DetectorRailBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final DetectorRailBlockProtocolAdapter INSTANCE = new DetectorRailBlockProtocolAdapter();

    public DetectorRailBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        boolean isPower = fromState.getValue(DetectorRailBlock.POWERED);
        return isPower ? 0b0001 : 0b0000;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isPower = (extraProtocolValue & 0b0001) == 0b0001;
        return fromState.setValue(DetectorRailBlock.POWERED, isPower);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}