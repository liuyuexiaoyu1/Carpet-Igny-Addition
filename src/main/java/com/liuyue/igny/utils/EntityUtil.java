package com.liuyue.igny.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Portal; //?>= 1.21.1
import net.minecraft.world.phys.AABB;
//?<= 1.20.6 ? import net.minecraft.world.level.block.Blocks;

public class EntityUtil {
    public static BlockPos findPortalInBoundingBox(ServerLevel level, AABB box) {
        int minX = net.minecraft.util.Mth.floor(box.minX);
        int minY = net.minecraft.util.Mth.floor(box.minY);
        int minZ = net.minecraft.util.Mth.floor(box.minZ);
        int maxX = net.minecraft.util.Mth.floor(box.maxX);
        int maxY = net.minecraft.util.Mth.floor(box.maxY);
        int maxZ = net.minecraft.util.Mth.floor(box.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (level.getBlockState(pos).getBlock() instanceof Portal) //#replace <= 1.20.6 ? if (level.getBlockState(pos).is(Blocks.END_PORTAL) || level.getBlockState(pos).is(Blocks.NETHER_PORTAL))
                    {
                        return pos;
                    }
                }
            }
        }
        return null;
    }
}
