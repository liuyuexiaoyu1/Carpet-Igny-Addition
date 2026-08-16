package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PoweredRailBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final PoweredRailBlockProtocolAdapter INSTANCE = new PoweredRailBlockProtocolAdapter();

    public PoweredRailBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int shapeOrdinal = fromState.getValue(PoweredRailBlock.SHAPE).ordinal();
        int bits =
                (shapeOrdinal & 0b0000_0111) |
                (0b0000_1000);
        if (fromState.getValue(PoweredRailBlock.POWERED)) {
            bits |= 0b0001_0000;
        }
        return bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int shapeOrdinal = (extraProtocolValue & 0b0000_0111) % 10;
        boolean noUpdate = (extraProtocolValue & 0b0000_1000) == 0b0000_1000;
        boolean powered = (extraProtocolValue & 0b0001_0000) == 0b0001_0000;

        if (noUpdate) {
            BetterEasyPlaceProtocolHandler.setPlaceFlag(BetterEasyPlaceProtocolHandler.EASY_PLACE_RAIL_BLOCK_NO_SHAPE_UPDATE);
        }

        return fromState
                .setValue(PoweredRailBlock.SHAPE, RailShape.values()[shapeOrdinal])
                .setValue(PoweredRailBlock.POWERED, powered);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}