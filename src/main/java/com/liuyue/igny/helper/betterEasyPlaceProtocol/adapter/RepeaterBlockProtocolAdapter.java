package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RepeaterBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final RepeaterBlockProtocolAdapter INSTANCE = new RepeaterBlockProtocolAdapter();

    public RepeaterBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int bits = (fromState.getValue(HorizontalDirectionalBlock.FACING).ordinal() - 2) & 0b0000_0011;
        bits |= ((fromState.getValue(RepeaterBlock.DELAY) - 1) & 0b0000_0011) << 2;
        if (fromState.getValue(RepeaterBlock.LOCKED)) {
            bits |= 0b0001_0000;
        }
        if (fromState.getValue(RepeaterBlock.POWERED)) {
            bits |= 0b0010_0000;
        }
        return bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        
        int facingIndex = (extraProtocolValue & 0b0000_0011) + 2;
        int delay = ((extraProtocolValue >>> 2) & 0b0000_0011) + 1;
        return fromState
                .setValue(HorizontalDirectionalBlock.FACING, Direction.values()[facingIndex])
                .setValue(RepeaterBlock.DELAY, delay)
                .setValue(RepeaterBlock.LOCKED, (extraProtocolValue & 0b0001_0000) != 0)
                .setValue(RepeaterBlock.POWERED, (extraProtocolValue & 0b0010_0000) != 0);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}