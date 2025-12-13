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
    private final int width;   // 内部宽度（x方向）
    private final int height;  // 内部高度（z方向）

    private EndPortalShape(Level level, BlockPos innerOrigin, int width, int height) {
        this.level = level;
        this.innerOrigin = innerOrigin;
        this.width = width;
        this.height = height;
    }

    public static Optional<EndPortalShape> findFromFrame(Level level, BlockPos eyeFramePos) {
        int max = IGNYSettings.maxEndPortalSize;
        if (max < 3) return Optional.empty();

        if (IGNYSettings.allowRectangularEndPortal) {
            for (int w = 3; w <= max; w++) {
                for (int h = 3; h <= max; h++) {
                    Optional<BlockPos> origin = tryFindRectangle(level, eyeFramePos, w, h);
                    if (origin.isPresent()) {
                        level.globalLevelEvent(1038, eyeFramePos.offset(1, 0, 1), 0);
                        return Optional.of(new EndPortalShape(level, origin.get(), w, h));
                    }
                }
            }
        } else {
            for (int size = 3; size <= max; size++) {
                Optional<BlockPos> origin = tryFindRectangle(level, eyeFramePos, size, size);
                if (origin.isPresent()) {
                    level.globalLevelEvent(1038, eyeFramePos.offset(1, 0, 1), 0);
                    return Optional.of(new EndPortalShape(level, origin.get(), size, size));
                }
            }
        }

        return Optional.empty();
    }

    private static Optional<BlockPos> tryFindRectangle(Level level, BlockPos eye, int innerWidth, int innerHeight) {
        int frameWidth = innerWidth + 2;
        int frameHeight = innerHeight + 2;
        int y = eye.getY();
        for (int minX = eye.getX() - frameWidth + 2; minX <= eye.getX() - 1; minX++) {
            int minZ = eye.getZ();
            int maxX = minX + frameWidth - 1;
            int maxZ = minZ + frameHeight - 1;
            if (eye.getX() <= minX || eye.getX() >= maxX) continue;
            BlockPos frameStart = new BlockPos(minX, y, minZ);
            if (validateFrame(level, frameStart, frameWidth, frameHeight)) {
                return Optional.of(frameStart.offset(1, 0, 1));
            }
        }
        for (int minX = eye.getX() - frameWidth + 2; minX <= eye.getX() - 1; minX++) {
            int maxZ = eye.getZ();
            int minZ = maxZ - frameHeight + 1;
            int maxX = minX + frameWidth - 1;
            if (eye.getX() <= minX || eye.getX() >= maxX) continue;
            BlockPos frameStart = new BlockPos(minX, y, minZ);
            if (validateFrame(level, frameStart, frameWidth, frameHeight)) {
                return Optional.of(frameStart.offset(1, 0, 1));
            }
        }
        for (int minZ = eye.getZ() - frameHeight + 2; minZ <= eye.getZ() - 1; minZ++) {
            int minX = eye.getX();
            int maxX = minX + frameWidth - 1;
            int maxZ = minZ + frameHeight - 1;
            if (eye.getZ() <= minZ || eye.getZ() >= maxZ) continue;
            BlockPos frameStart = new BlockPos(minX, y, minZ);
            if (validateFrame(level, frameStart, frameWidth, frameHeight)) {
                return Optional.of(frameStart.offset(1, 0, 1));
            }
        }
        for (int minZ = eye.getZ() - frameHeight + 2; minZ <= eye.getZ() - 1; minZ++) {
            int maxX = eye.getX();
            int minX = maxX - frameWidth + 1;
            int maxZ = minZ + frameHeight - 1;
            if (eye.getZ() <= minZ || eye.getZ() >= maxZ) continue;
            BlockPos frameStart = new BlockPos(minX, y, minZ);
            if (validateFrame(level, frameStart, frameWidth, frameHeight)) {
                return Optional.of(frameStart.offset(1, 0, 1));
            }
        }

        return Optional.empty();
    }

    private static boolean validateFrame(Level level, BlockPos start, int frameWidth, int frameHeight) {
        int minX = start.getX();
        int minZ = start.getZ();
        int maxX = minX + frameWidth - 1;
        int maxZ = minZ + frameHeight - 1;
        for (int x = minX + 1; x < maxX; x++) {
            if (!isValidEyeFrame(level, new BlockPos(x, start.getY(), minZ), Direction.SOUTH)) return false;
        }
        for (int x = minX + 1; x < maxX; x++) {
            if (!isValidEyeFrame(level, new BlockPos(x, start.getY(), maxZ), Direction.NORTH)) return false;
        }
        for (int z = minZ + 1; z < maxZ; z++) {
            if (!isValidEyeFrame(level, new BlockPos(minX, start.getY(), z), Direction.EAST)) return false;
        }
        for (int z = minZ + 1; z < maxZ; z++) {
            if (!isValidEyeFrame(level, new BlockPos(maxX, start.getY(), z), Direction.WEST)) return false;
        }

        return true;
    }

    private static boolean isValidEyeFrame(Level level, BlockPos pos, Direction expectedFacing) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof EndPortalFrameBlock)) return false;
        if (!state.getValue(EndPortalFrameBlock.HAS_EYE)) return false;
        Direction actual = state.getValue(EndPortalFrameBlock.FACING);
        return actual == expectedFacing;
    }

    public void createPortal() {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                BlockPos pos = innerOrigin.offset(x, 0, z);
                level.setBlock(pos, Blocks.END_PORTAL.defaultBlockState(), 18);
            }
        }
    }
}