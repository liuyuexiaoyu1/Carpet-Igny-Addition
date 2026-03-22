package com.liuyue.igny.utils.deobfuscator.yarn;

/**
 * Copy from Carpet-TIS-Addition
 * 此文件以LGPL-3.0协议开源
 */

public class YarnVersionCache {
    public final String minecraftVersion;
    public final String yarnVersion;

    public YarnVersionCache(String minecraftVersion, String yarnVersion) {
        this.minecraftVersion = minecraftVersion;
        this.yarnVersion = yarnVersion;
    }
}