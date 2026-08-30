package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BellBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final BellBlockProtocolAdapter INSTANCE = new BellBlockProtocolAdapter();

    private static final int BIT_FACING_MASK = 0b111;
    private static final int BIT_ATTACHMENT_MASK = 0b0011_0000;
    private static final int BIT_ATTACHMENT_SHIFT = 4;

    public BellBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int attachmentOrdinal = fromState.getValue(BellBlock.ATTACHMENT).ordinal();
        int facingBits = ((horizontalFacingIndex(fromState.getValue(BellBlock.FACING)) + 1) & BIT_FACING_MASK);
        int bits = facingBits | ((attachmentOrdinal & 0b11) << BIT_ATTACHMENT_SHIFT);
        return protocolValue | bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int attachmentOrdinal = ((extraProtocolValue & BIT_ATTACHMENT_MASK) >>> BIT_ATTACHMENT_SHIFT) % 3;
        BlockState state = fromState.setValue(BellBlock.ATTACHMENT, BellAttachType.values()[attachmentOrdinal]);

        int facingIndex = (extraProtocolValue & BIT_FACING_MASK) - 1;
        if (facingIndex >= 0 && facingIndex <= 3) {
            Direction facing = horizontalFacing(facingIndex);
            if (BellBlock.FACING.getPossibleValues().contains(facing)) {
                state = state.setValue(BellBlock.FACING, facing);
            }
        }
        return state;
    }

    private static int horizontalFacingIndex(Direction facing) {
        return switch (facing) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
    }

    private static Direction horizontalFacing(int index) {
        return switch (index) {
            case 1 -> Direction.EAST;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}
