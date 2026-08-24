package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WallBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final WallBlockProtocolAdapter INSTANCE = new WallBlockProtocolAdapter();

    public WallBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        //#if MC >= 12105
        //$$ WallSide north = fromState.getValue(WallBlock.NORTH);
        //$$ WallSide east = fromState.getValue(WallBlock.EAST);
        //$$ WallSide south = fromState.getValue(WallBlock.SOUTH);
        //$$ WallSide west = fromState.getValue(WallBlock.WEST);
        //#else
        WallSide north = fromState.getValue(WallBlock.NORTH_WALL);
        WallSide east = fromState.getValue(WallBlock.EAST_WALL);
        WallSide south = fromState.getValue(WallBlock.SOUTH_WALL);
        WallSide west = fromState.getValue(WallBlock.WEST_WALL);
        //#endif
        boolean up = fromState.getValue(WallBlock.UP);

        int bits = 0;
        bits |= (north.ordinal() & 0b11);
        bits |= (east.ordinal() & 0b11) << 2;
        bits |= (south.ordinal() & 0b11) << 4;
        bits |= (west.ordinal() & 0b11) << 6;
        if (up) bits |= 1 << 8;

        return bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        WallSide north = WallSide.values()[((extraProtocolValue) & 0b11) % 3];
        WallSide east = WallSide.values()[((extraProtocolValue >> 2) & 0b11) % 3];
        WallSide south = WallSide.values()[((extraProtocolValue >> 4) & 0b11) % 3];
        WallSide west = WallSide.values()[((extraProtocolValue >> 6) & 0b11) % 3];
        boolean up = ((extraProtocolValue >> 8) & 0b1) == 0b1;

        return fromState
                //#if MC >= 12105
                //$$ .setValue(WallBlock.NORTH, north)
                //$$ .setValue(WallBlock.EAST, east)
                //$$ .setValue(WallBlock.SOUTH, south)
                //$$ .setValue(WallBlock.WEST, west)
                //#else
                .setValue(WallBlock.NORTH_WALL, north)
                .setValue(WallBlock.EAST_WALL, east)
                .setValue(WallBlock.SOUTH_WALL, south)
                .setValue(WallBlock.WEST_WALL, west)
                //#endif
                .setValue(WallBlock.UP, up);
    }

    @Override
    public @NotNull BlockProtocolStateAdapter.ProtocolType igny$getProtocolType() {
        return BlockProtocolStateAdapter.ProtocolType.ADDED;
    }
}