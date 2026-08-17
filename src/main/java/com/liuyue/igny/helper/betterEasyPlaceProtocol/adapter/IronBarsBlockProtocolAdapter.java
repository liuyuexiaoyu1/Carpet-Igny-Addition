package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IronBarsBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final IronBarsBlockProtocolAdapter INSTANCE = new IronBarsBlockProtocolAdapter();

    public IronBarsBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        boolean north = fromState.getValue(IronBarsBlock.NORTH);
        boolean east = fromState.getValue(IronBarsBlock.EAST);
        boolean south = fromState.getValue(IronBarsBlock.SOUTH);
        boolean west = fromState.getValue(IronBarsBlock.WEST);
        int bits = 0;
        if (north) bits |= 1;
        if (east) bits |= 1 << 1;
        if (south) bits |= 1 << 2;
        if (west) bits |= 1 << 3;

        return bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean north = ((extraProtocolValue) & 0b1) == 0b1;
        boolean east = ((extraProtocolValue >> 1) & 0b1) == 0b1;
        boolean south = ((extraProtocolValue >> 2) & 0b1) == 0b1;
        boolean west = ((extraProtocolValue >> 3) & 0b1) == 0b1;

        return fromState.setValue(IronBarsBlock.NORTH, north)
                .setValue(IronBarsBlock.EAST, east)
                .setValue(IronBarsBlock.SOUTH, south)
                .setValue(IronBarsBlock.WEST, west);
    }

    @Override
    public @NotNull BlockProtocolStateAdapter.ProtocolType igny$getProtocolType() {
        return BlockProtocolStateAdapter.ProtocolType.REPLACE;
    }
}