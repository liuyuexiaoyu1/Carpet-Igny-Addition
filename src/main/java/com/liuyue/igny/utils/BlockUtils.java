package com.liuyue.igny.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockUtils {
    public static String getDisplayName(BlockState state) {
        try {
            Component comp = state.getBlock().getName();
            return comp.getString();
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static String getBlockRegistryName(Block block) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        return key.toString();
    }

    public static Component getTranslatedName(Block block) {
        if (block == null) {
            return Component.literal("空气");
        }
        try {
            return Component.translatable(block.getDescriptionId());
        } catch (Exception e) {
            return Component.literal("未知方块");
        }
    }
}