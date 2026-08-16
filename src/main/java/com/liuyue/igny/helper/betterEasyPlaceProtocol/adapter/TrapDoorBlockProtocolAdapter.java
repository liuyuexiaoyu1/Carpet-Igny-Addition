package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrapDoorBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final TrapDoorBlockProtocolAdapter INSTANCE = new TrapDoorBlockProtocolAdapter();

    public TrapDoorBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int facingOrdinal = fromState.getValue(TrapDoorBlock.FACING).ordinal() - 2;
        int halfOrdinal = fromState.getValue(TrapDoorBlock.HALF).ordinal();
        boolean isOpen = fromState.getValue(TrapDoorBlock.OPEN);
        return (facingOrdinal & 0b0000_0011) |
        (halfOrdinal & 0b0000_0001) << 2 |
        (isOpen ? 0b0000_1000 : 0b0000_0000);
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int facingOrdinal = (extraProtocolValue & 0b0000_0011) + 2;
        int halfOrdinal = (extraProtocolValue & 0b0000_0100) >>> 2;
        boolean isOpen = (extraProtocolValue & 0b0000_1000) == 0b0000_1000;

        BlockState newState = fromState
                .setValue(TrapDoorBlock.FACING, Direction.values()[facingOrdinal])
                .setValue(TrapDoorBlock.HALF, Half.values()[halfOrdinal]);

        if (!fromState.getBlock().equals(Blocks.IRON_TRAPDOOR)) {
            newState = newState.setValue(TrapDoorBlock.OPEN, isOpen);
        }

        return newState;
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}