package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RailBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final RailBlockProtocolAdapter INSTANCE = new RailBlockProtocolAdapter();

    public RailBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int shapeOrdinal = fromState.getValue(RailBlock.SHAPE).ordinal();
        return (shapeOrdinal & 0b0000_1111) |
        (0b0001_0000);
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int shapeOrdinal = (extraProtocolValue & 0b0000_1111) % 10;
        boolean noUpdate = (extraProtocolValue & 0b0001_0000) == 0b0001_0000;

        if (noUpdate) {
            BetterEasyPlaceProtocolHandler.setPlaceFlag(BetterEasyPlaceProtocolHandler.EASY_PLACE_RAIL_BLOCK_NO_SHAPE_UPDATE);
        }

        return fromState.setValue(RailBlock.SHAPE, RailShape.values()[shapeOrdinal]);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}