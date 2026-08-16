package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DoorBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final DoorBlockProtocolAdapter INSTANCE = new DoorBlockProtocolAdapter();

    public DoorBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int facingOrdinal = fromState.getValue(DoorBlock.FACING).ordinal() - 2;
        int hingeOrdinal = fromState.getValue(DoorBlock.HINGE).ordinal();
        boolean isOpen = fromState.getValue(DoorBlock.OPEN);
        return (facingOrdinal & 0b0000_0011) |
        (hingeOrdinal & 0b0000_0001) << 2 |
        (isOpen ? 0b0000_1000 : 0b0000_0000);
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int facingOrdinal = (extraProtocolValue & 0b0000_0011) + 2;
        int hingeOrdinal = (extraProtocolValue & 0b0000_0100) >>> 2;
        boolean isOpen = (extraProtocolValue & 0b0000_1000) == 0b0000_1000;

        BlockState newState = fromState
                .setValue(DoorBlock.FACING, Direction.values()[facingOrdinal])
                .setValue(DoorBlock.HINGE, DoorHingeSide.values()[hingeOrdinal]);

        if (!fromState.getBlock().equals(Blocks.IRON_DOOR)) {
            newState = newState.setValue(DoorBlock.OPEN, isOpen);
        }

        return newState;
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}