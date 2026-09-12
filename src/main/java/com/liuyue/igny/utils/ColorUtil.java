package com.liuyue.igny.utils;

import net.minecraft.world.item.DyeColor;

public final class ColorUtil {

    public static int of(DyeColor color) {
        //#if MC >= 12101
        return color.getTextureDiffuseColor();
        //#else
        //$$ float[] rgb = color.getTextureDiffuseColors();
        //$$ return 0xFF000000
        //$$         | ((int) (rgb[0] * 255.0F) << 16)
        //$$         | ((int) (rgb[1] * 255.0F) << 8)
        //$$         | (int) (rgb[2] * 255.0F);
        //#endif
    }

    public static String labelOf(int rgb) {
        return String.format("#%06X", rgb & 0xFFFFFF);
    }
}
