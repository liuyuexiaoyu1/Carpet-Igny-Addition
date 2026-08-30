package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PistonBaseBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final PistonBaseBlockProtocolAdapter INSTANCE = new PistonBaseBlockProtocolAdapter();

    public PistonBaseBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int bits = protocolValue;
        if (fromState.getValue(PistonBaseBlock.EXTENDED)) {
            bits |= 0b0001_0000;
        }
        bits |= ((fromState.getValue(PistonBaseBlock.FACING).ordinal() + 1) & 0b0000_0111) << 5;
        return bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isExtended = (extraProtocolValue & 0b0001_0000) == 0b0001_0000;
        int facingIndex = ((extraProtocolValue >>> 5) & 0b0000_0111) - 1;
        if (facingIndex < 0) {
            facingIndex = 0;
        }
        Direction facing = Direction.values()[facingIndex % 6];

        if (isExtended) {
            BetterEasyPlaceProtocolHandler.setPlaceFlag(BetterEasyPlaceProtocolHandler.EASY_PLACE_PISTON_NO_UPDATE);
            
            if ((extraProtocolValue & 0b1_0000_0000) != 0) {
                BetterEasyPlaceProtocolHandler.setPlaceFlag(BetterEasyPlaceProtocolHandler.EASY_PLACE_PISTON_PLACE_HEAD);
            }
        }

        return fromState
                .setValue(PistonBaseBlock.EXTENDED, isExtended)
                .setValue(PistonBaseBlock.FACING, facing);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}