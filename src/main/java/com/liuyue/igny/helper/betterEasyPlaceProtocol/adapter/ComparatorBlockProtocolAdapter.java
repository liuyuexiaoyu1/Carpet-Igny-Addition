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
        Direction facing = fromState.getValue(HorizontalDirectionalBlock.FACING);
        int facingBits = switch (facing) {
            case NORTH -> 0b00;
            case EAST -> 0b01;
            case SOUTH -> 0b10;
            case WEST -> 0b11;
            default -> 0b00;
        };
        boolean isSubtract = fromState.getValue(ComparatorBlock.MODE) == ComparatorMode.SUBTRACT;
        int modeBits = isSubtract ? 0b0100 : 0;
        boolean isPowered = fromState.getValue(ComparatorBlock.POWERED);
        int poweredBits = isPowered ? 0b1000 : 0;
        return (protocolValue & ~0b1111) | facingBits | modeBits | poweredBits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int facingBits = extraProtocolValue & 0b0011;
        Direction facing = switch (facingBits) {
            case 0 -> Direction.NORTH;
            case 1 -> Direction.EAST;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.WEST;
            default -> Direction.NORTH;
        };
        ComparatorMode mode = (extraProtocolValue & 0b0100) != 0
                ? ComparatorMode.SUBTRACT
                : ComparatorMode.COMPARE;
        boolean isPowered = (extraProtocolValue & 0b1000) != 0;

        return fromState
                .setValue(HorizontalDirectionalBlock.FACING, facing)
                .setValue(ComparatorBlock.MODE, mode)
                .setValue(ComparatorBlock.POWERED, isPowered);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}