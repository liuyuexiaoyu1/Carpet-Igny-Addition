package com.liuyue.igny.utils;

import net.minecraft.world.item.DyeColor;
//#if >= 26.4
/*$$import net.minecraft.util.CommonColors;
import java.util.Map;$$*/
//#endif

public final class ColorUtil {
    //#if >= 26.4
    /*$$private final static Map<DyeColor, Integer> colorMap = Map.ofEntries(
            Map.entry(DyeColor.WHITE, CommonColors.TEXTURE_TINT_COLORS.white()),
            Map.entry(DyeColor.ORANGE, CommonColors.TEXTURE_TINT_COLORS.orange()),
            Map.entry(DyeColor.MAGENTA, CommonColors.TEXTURE_TINT_COLORS.magenta()),
            Map.entry(DyeColor.LIGHT_BLUE, CommonColors.TEXTURE_TINT_COLORS.lightBlue()),
            Map.entry(DyeColor.YELLOW, CommonColors.TEXTURE_TINT_COLORS.yellow()),
            Map.entry(DyeColor.LIME, CommonColors.TEXTURE_TINT_COLORS.lime()),
            Map.entry(DyeColor.PINK, CommonColors.TEXTURE_TINT_COLORS.pink()),
            Map.entry(DyeColor.GRAY, CommonColors.TEXTURE_TINT_COLORS.gray()),
            Map.entry(DyeColor.LIGHT_GRAY, CommonColors.TEXTURE_TINT_COLORS.lightGray()),
            Map.entry(DyeColor.CYAN, CommonColors.TEXTURE_TINT_COLORS.cyan()),
            Map.entry(DyeColor.PURPLE, CommonColors.TEXTURE_TINT_COLORS.purple()),
            Map.entry(DyeColor.BLUE, CommonColors.TEXTURE_TINT_COLORS.blue()),
            Map.entry(DyeColor.BROWN, CommonColors.TEXTURE_TINT_COLORS.brown()),
            Map.entry(DyeColor.GREEN, CommonColors.TEXTURE_TINT_COLORS.green()),
            Map.entry(DyeColor.RED, CommonColors.TEXTURE_TINT_COLORS.red()),
            Map.entry(DyeColor.BLACK, CommonColors.TEXTURE_TINT_COLORS.black())
    );$$*/
    //#endif

    public static int of(DyeColor color) {
        //#if >= 26.4
        //$$ return colorMap.get(color);
        //#elseif >= 1.21.1
        return color.getTextureDiffuseColor();
        //#else
        /*$$float[] rgb = color.getTextureDiffuseColors();
        return 0xFF000000
                | ((int) (rgb[0] * 255.0F) << 16)
                | ((int) (rgb[1] * 255.0F) << 8)
        | (int) (rgb[2] * 255.0F);$$*/
        //#endif
    }

    public static String labelOf(int rgb) {
        return String.format("#%06X", rgb & 0xFFFFFF);
    }
}
