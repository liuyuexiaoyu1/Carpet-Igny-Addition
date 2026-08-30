package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FaceAttachedHorizontalDirectionalBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final FaceAttachedHorizontalDirectionalBlockProtocolAdapter INSTANCE = new FaceAttachedHorizontalDirectionalBlockProtocolAdapter();

    private static final int BIT_FACING_MASK = 0b111;
    private static final int BIT_FACE_MASK = 0b0011_0000;
    private static final int BIT_FACE_SHIFT = 4;

    public FaceAttachedHorizontalDirectionalBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int faceOrdinal = fromState.getValue(FaceAttachedHorizontalDirectionalBlock.FACE).ordinal();
        int facingBits = ((horizontalFacingIndex(fromState.getValue(FaceAttachedHorizontalDirectionalBlock.FACING)) + 1) & BIT_FACING_MASK);
        int bits = facingBits | ((faceOrdinal & 0b11) << BIT_FACE_SHIFT);
        return protocolValue | bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int faceOrdinal = ((extraProtocolValue & BIT_FACE_MASK) >>> BIT_FACE_SHIFT) % 3;
        BlockState state = fromState.setValue(FaceAttachedHorizontalDirectionalBlock.FACE, AttachFace.values()[faceOrdinal]);

        int facingIndex = (extraProtocolValue & BIT_FACING_MASK) - 1;
        if (facingIndex >= 0 && facingIndex <= 3) {
            Direction facing = horizontalFacing(facingIndex);
            if (FaceAttachedHorizontalDirectionalBlock.FACING.getPossibleValues().contains(facing)) {
                state = state.setValue(FaceAttachedHorizontalDirectionalBlock.FACING, facing);
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
