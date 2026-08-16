package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneLampBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final RedstoneLampBlockProtocolAdapter INSTANCE = new RedstoneLampBlockProtocolAdapter();

    public RedstoneLampBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        boolean isLit = fromState.getValue(RedstoneLampBlock.LIT);
        int bits = (isLit ? 0b0001_0000 : 0b0000_0000);
        return protocolValue | bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isLit = (extraProtocolValue & 0b0001_0000) == 0b0001_0000;
        return fromState.setValue(RedstoneLampBlock.LIT, isLit);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}