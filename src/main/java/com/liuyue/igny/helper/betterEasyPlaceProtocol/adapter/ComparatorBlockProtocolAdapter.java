package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComparatorBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final ComparatorBlockProtocolAdapter INSTANCE = new ComparatorBlockProtocolAdapter();

    public ComparatorBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int bits = (fromState.getValue(HorizontalDirectionalBlock.FACING).ordinal() - 2) & 0b0000_0011;
        if (fromState.getValue(ComparatorBlock.MODE) == ComparatorMode.SUBTRACT) {
            bits |= 0b0000_0100;
        }
        if (fromState.getValue(ComparatorBlock.POWERED)) {
            bits |= 0b0000_1000;
        }
        return bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        
        int facingIndex = (extraProtocolValue & 0b0000_0011) + 2;
        return fromState
                .setValue(HorizontalDirectionalBlock.FACING, Direction.values()[facingIndex])
                .setValue(ComparatorBlock.MODE, (extraProtocolValue & 0b0000_0100) != 0 ? ComparatorMode.SUBTRACT : ComparatorMode.COMPARE)
                .setValue(ComparatorBlock.POWERED, (extraProtocolValue & 0b0000_1000) != 0);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}