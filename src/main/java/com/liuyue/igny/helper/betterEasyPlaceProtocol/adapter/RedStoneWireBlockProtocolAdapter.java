package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
//#if MC >= 26.3
//$$ import net.minecraft.world.level.block.RedstoneWireBlock;
//#else
import net.minecraft.world.level.block.RedStoneWireBlock;
//#endif
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedStoneWireBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final RedStoneWireBlockProtocolAdapter INSTANCE = new RedStoneWireBlockProtocolAdapter();

    private static final int BIT_IS_DOT = 0b0001;

    public RedStoneWireBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        //#if MC >= 26.3
        //$$ boolean isDot = !fromState.getValue(RedstoneWireBlock.NORTH).isConnected()
        //$$         && !fromState.getValue(RedstoneWireBlock.EAST).isConnected()
        //$$         && !fromState.getValue(RedstoneWireBlock.SOUTH).isConnected()
        //$$         && !fromState.getValue(RedstoneWireBlock.WEST).isConnected();
        //#else
        boolean isDot = !fromState.getValue(RedStoneWireBlock.NORTH).isConnected()
                && !fromState.getValue(RedStoneWireBlock.EAST).isConnected()
                && !fromState.getValue(RedStoneWireBlock.SOUTH).isConnected()
                && !fromState.getValue(RedStoneWireBlock.WEST).isConnected();
        //#endif
        return isDot ? BIT_IS_DOT : 0b0000;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isDot = (extraProtocolValue & BIT_IS_DOT) == BIT_IS_DOT;
        if (!isDot) {
            return fromState;
        }
        //#if MC >= 26.3
        //$$ return fromState.setValue(RedstoneWireBlock.NORTH, RedstoneSide.NONE)
        //$$         .setValue(RedstoneWireBlock.EAST, RedstoneSide.NONE)
        //$$         .setValue(RedstoneWireBlock.SOUTH, RedstoneSide.NONE)
        //$$         .setValue(RedstoneWireBlock.WEST, RedstoneSide.NONE);
        //#else
        return fromState.setValue(RedStoneWireBlock.NORTH, RedstoneSide.NONE)
                .setValue(RedStoneWireBlock.EAST, RedstoneSide.NONE)
                .setValue(RedStoneWireBlock.SOUTH, RedstoneSide.NONE)
                .setValue(RedStoneWireBlock.WEST, RedstoneSide.NONE);
        //#endif
    }

    @Override
    public @NotNull BlockProtocolStateAdapter.ProtocolType igny$getProtocolType() {
        return BlockProtocolStateAdapter.ProtocolType.ADDED;
    }
}
