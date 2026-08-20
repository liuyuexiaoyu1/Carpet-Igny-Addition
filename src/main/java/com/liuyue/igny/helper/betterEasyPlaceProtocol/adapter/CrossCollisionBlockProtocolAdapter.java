package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrossCollisionBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final CrossCollisionBlockProtocolAdapter INSTANCE = new CrossCollisionBlockProtocolAdapter();

    public CrossCollisionBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        boolean north = fromState.getValue(CrossCollisionBlock.NORTH);
        boolean east = fromState.getValue(CrossCollisionBlock.EAST);
        boolean south = fromState.getValue(CrossCollisionBlock.SOUTH);
        boolean west = fromState.getValue(CrossCollisionBlock.WEST);
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

        return fromState.setValue(CrossCollisionBlock.NORTH, north)
                .setValue(CrossCollisionBlock.EAST, east)
                .setValue(CrossCollisionBlock.SOUTH, south)
                .setValue(CrossCollisionBlock.WEST, west);
    }

    @Override
    public @NotNull BlockProtocolStateAdapter.ProtocolType igny$getProtocolType() {
        return BlockProtocolStateAdapter.ProtocolType.REPLACE;
    }
}