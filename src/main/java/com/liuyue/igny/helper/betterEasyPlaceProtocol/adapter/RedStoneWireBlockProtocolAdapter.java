package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedStoneWireBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final RedStoneWireBlockProtocolAdapter INSTANCE = new RedStoneWireBlockProtocolAdapter();

    public RedStoneWireBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        boolean isDot = !fromState.getValue(RedStoneWireBlock.NORTH).isConnected()
                && !fromState.getValue(RedStoneWireBlock.EAST).isConnected()
                && !fromState.getValue(RedStoneWireBlock.SOUTH).isConnected()
                && !fromState.getValue(RedStoneWireBlock.WEST).isConnected();
        return isDot ? 0b0001 : 0b0000;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isDot = (extraProtocolValue & 0b0001) == 0b0001;
        if (isDot) {
            return fromState.setValue(RedStoneWireBlock.NORTH, RedstoneSide.NONE)
                    .setValue(RedStoneWireBlock.EAST, RedstoneSide.NONE)
                    .setValue(RedStoneWireBlock.SOUTH, RedstoneSide.NONE)
                    .setValue(RedStoneWireBlock.WEST, RedstoneSide.NONE);
        }
        return fromState;
    }

    @Override
    public @NotNull BlockProtocolStateAdapter.ProtocolType igny$getProtocolType() {
        return BlockProtocolStateAdapter.ProtocolType.REPLACE;
    }
}
