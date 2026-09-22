package com.liuyue.igny.utils;

import net.minecraft.WorldVersion;
import net.minecraft.SharedConstants;

/**
 * Copy from Carpet-TIS-Addition
 * 此文件以LGPL-3.0协议开源
 */

public class EnvironmentUtil {
    public static WorldVersion getMinecraftVersion()
    {
        return SharedConstants.getCurrentVersion();
    }

    public static String getMinecraftVersionId()
    {
        return getMinecraftVersion().getId(); //#replace >= 1.21.8 ? return getMinecraftVersion().id();
    }

    @SuppressWarnings("unused")
    public static String getMinecraftVersionName()
    {
        return getMinecraftVersion().getName(); //#replace >= 1.21.8 ? return getMinecraftVersion().name();
    }

    @SuppressWarnings("unused")
    public static boolean isMinecraftUnobfuscated()
    {
        return getMinecraftVersionId().endsWith("_unobfuscated"); //#replace >= 26.1 ? return true;
    }

    @SuppressWarnings("unused")
    public static boolean isMinecraftObfuscated()
    {
        return !isMinecraftUnobfuscated();
    }
}