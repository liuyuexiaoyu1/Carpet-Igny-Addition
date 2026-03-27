package com.liuyue.igny.utils;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Optional;

public class EndPortalShape {
    private final Level level;
    private final BlockPos innerOrigin;
    private final int width;
    private final int height;

    private EndPortalShape(Level level, BlockPos innerOrigin, int width, int height) {
        this.level = level;
        this.innerOrigin = innerOrigin;
        this.width = width;
        this.height = height;
    }

    public static Optional<EndPortalShape> findFromFrame(Level level, BlockPos eyePosition) {
        BlockState state = level.getBlockState(eyePosition);
        if (!(state.getBlock() instanceof EndPortalFrameBlock)) {
            return Optional.empty();
        }
        int maxEndPortalSize = IGNYSettings.maxEndPortalSize;
        Direction facing = state.getValue(EndPortalFrameBlock.FACING);
        Direction leftFacing = facing.getAxis() == Direction.Axis.Z ? Direction.EAST : Direction.SOUTH;
        int minOffset = 0;
        while (minOffset > -maxEndPortalSize && isValidFrame(level, eyePosition.relative(leftFacing, minOffset - 1), facing)) {
            minOffset--;
        }

        int maxOffset = 0;
        while (maxOffset < maxEndPortalSize && isValidFrame(level, eyePosition.relative(leftFacing, maxOffset + 1), facing)) {
            maxOffset++;
        }

        int currentSideLength = maxOffset - minOffset + 1;

        if (currentSideLength < 3) {
            return Optional.empty();
        }
        for (int depth = 3; depth <= maxEndPortalSize; depth++) {
            if (!IGNYSettings.allowRectangularEndPortal && depth != currentSideLength) {
                continue;
            }
            BlockPos oppositeStart = eyePosition.relative(leftFacing, minOffset).relative(facing, depth + 1);
            Direction oppositeFacing = facing.getOpposite();
            if (validateSide(level, oppositeStart, leftFacing, currentSideLength, oppositeFacing)) {
                Direction rightFacing = leftFacing.getOpposite();
                BlockPos leftSideStart = eyePosition.relative(leftFacing, minOffset - 1).relative(facing, 1);
                BlockPos rightSideStart = eyePosition.relative(leftFacing, maxOffset + 1).relative(facing, 1);
                if (validateSide(level, leftSideStart, facing, depth, leftFacing) &&
                        validateSide(level, rightSideStart, facing, depth, rightFacing)) {
                    BlockPos point1 = eyePosition.relative(leftFacing, minOffset).relative(facing, 1);
                    BlockPos point2 = eyePosition.relative(leftFacing, maxOffset).relative(facing, depth);
                    int minX = Math.min(point1.getX(), point2.getX());
                    int minZ = Math.min(point1.getZ(), point2.getZ());
                    BlockPos correctedOrigin = new BlockPos(minX, eyePosition.getY(), minZ);
                    int finalWidth = leftFacing.getAxis() == Direction.Axis.X ? currentSideLength : depth;
                    int finalHeight = leftFacing.getAxis() == Direction.Axis.X ? depth : currentSideLength;
                    level.globalLevelEvent(1038, eyePosition.offset(1, 0, 1), 0);
                    return Optional.of(new EndPortalShape(level, correctedOrigin, finalWidth, finalHeight));
                }
            }
        }
        return Optional.empty();
    }

    private static boolean validateSide(Level level, BlockPos start, Direction direction, int length, Direction expectedFacing) {
        for (int i = 0; i < length; i++) {
            BlockPos currentPosition = start.relative(direction, i);
            if (!isValidFrame(level, currentPosition, expectedFacing)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidFrame(Level level, BlockPos position, Direction expectedFacing) {
        BlockState state = level.getBlockState(position);
        return state.is(Blocks.END_PORTAL_FRAME) &&
                state.getValue(EndPortalFrameBlock.HAS_EYE) &&
                state.getValue(EndPortalFrameBlock.FACING) == expectedFacing;
    }

    public void createPortal() {
        BlockState portalState = Blocks.END_PORTAL.defaultBlockState();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                level.setBlock(innerOrigin.offset(x, 0, z), portalState, 2);
            }
        }
    }
}