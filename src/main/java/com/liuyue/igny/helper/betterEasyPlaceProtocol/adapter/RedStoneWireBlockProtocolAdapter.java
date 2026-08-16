package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper;
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
        int bits = 0;
        if (fromState.getValue(RedStoneWireBlock.NORTH) != RedstoneSide.NONE) bits |= 0b0001;
        if (fromState.getValue(RedStoneWireBlock.EAST) != RedstoneSide.NONE) bits |= 0b0010;
        if (fromState.getValue(RedStoneWireBlock.SOUTH) != RedstoneSide.NONE) bits |= 0b0100;
        if (fromState.getValue(RedStoneWireBlock.WEST) != RedstoneSide.NONE) bits |= 0b1000;
        return bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean north = (extraProtocolValue & 0b0001) != 0;
        boolean east = (extraProtocolValue & 0b0010) != 0;
        boolean south = (extraProtocolValue & 0b0100) != 0;
        boolean west = (extraProtocolValue & 0b1000) != 0;

        
        int upBits = 0;
        double relativeHitZ = EasyPlaceExtraProtocolHelper.getRelativeHitZ(context.getClickLocation(), context.getClickedPos());
        if (EasyPlaceExtraProtocolHelper.isProtocol(relativeHitZ)) {
            upBits = EasyPlaceExtraProtocolHelper.decodeProtocolValueFromHitDim(relativeHitZ);
        }
        RedstoneSide northSide = north ? ((upBits & 0b0001) != 0 ? RedstoneSide.UP : RedstoneSide.SIDE) : RedstoneSide.NONE;
        RedstoneSide eastSide = east ? ((upBits & 0b0010) != 0 ? RedstoneSide.UP : RedstoneSide.SIDE) : RedstoneSide.NONE;
        RedstoneSide southSide = south ? ((upBits & 0b0100) != 0 ? RedstoneSide.UP : RedstoneSide.SIDE) : RedstoneSide.NONE;
        RedstoneSide westSide = west ? ((upBits & 0b1000) != 0 ? RedstoneSide.UP : RedstoneSide.SIDE) : RedstoneSide.NONE;

        
        long prop = BetterEasyPlaceProtocolHandler.getPlaceProperty();
        prop |= BetterEasyPlaceProtocolHandler.EASY_PLACE_REDSTONE_WIRE_NO_UPDATE;
        prop |= (long) northSide.ordinal() << 4;
        prop |= (long) eastSide.ordinal() << 6;
        prop |= (long) southSide.ordinal() << 8;
        prop |= (long) westSide.ordinal() << 10;
        BetterEasyPlaceProtocolHandler.setPlaceProperty(prop);

        return fromState
                .setValue(RedStoneWireBlock.NORTH, northSide)
                .setValue(RedStoneWireBlock.EAST, eastSide)
                .setValue(RedStoneWireBlock.SOUTH, southSide)
                .setValue(RedStoneWireBlock.WEST, westSide);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}