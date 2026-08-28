package com.liuyue.igny.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.phys.AABB;
//#if MC <= 12006
//$$ import net.minecraft.world.level.block.Blocks;
//#endif

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
                    //#if MC <= 12006
                    //$$ if (level.getBlockState(pos).is(Blocks.END_PORTAL) || level.getBlockState(pos).is(Blocks.NETHER_PORTAL))
                    //#else
                    if (level.getBlockState(pos).getBlock() instanceof Portal)
                    //#endif
                    {
                        return pos;
                    }
                }
            }
        }
        return null;
    }
}
