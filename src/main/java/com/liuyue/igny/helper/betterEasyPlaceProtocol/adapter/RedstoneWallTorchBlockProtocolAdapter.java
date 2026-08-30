package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneWallTorchBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final RedstoneWallTorchBlockProtocolAdapter INSTANCE = new RedstoneWallTorchBlockProtocolAdapter();

    private static final int BIT_FACING_MASK = 0b111;
    private static final int BIT_LIT = 0b1000;

    public RedstoneWallTorchBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        boolean isLit = fromState.getValue(RedstoneWallTorchBlock.LIT);
        int facingBits = ((horizontalFacingIndex(fromState.getValue(RedstoneWallTorchBlock.FACING)) + 1) & BIT_FACING_MASK);
        int bits = facingBits | (isLit ? BIT_LIT : 0);
        return protocolValue | bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isLit = (extraProtocolValue & BIT_LIT) == BIT_LIT;
        BlockState state = fromState.setValue(RedstoneWallTorchBlock.LIT, isLit);

        int facingIndex = (extraProtocolValue & BIT_FACING_MASK) - 1;
        if (facingIndex >= 0 && facingIndex <= 3) {
            Direction facing = horizontalFacing(facingIndex);
            if (RedstoneWallTorchBlock.FACING.getPossibleValues().contains(facing)) {
                state = state.setValue(RedstoneWallTorchBlock.FACING, facing);
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
