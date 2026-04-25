package com.liuyue.igny.utils;

import carpet.CarpetSettings;
import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;

import java.util.List;
import java.util.Set;

public class IGNYMixinConfigPlugin extends RestrictiveMixinConfigPlugin {
    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("com.liuyue.igny.mixins.carpet.fix.fakePlayerMemoryLeakFix")) {
            if (CarpetSettings.carpetVersion.length() >= 6) {
                if (Integer.decode(CarpetSettings.carpetVersion.substring(CarpetSettings.carpetVersion.length() - 6)) >= 260326) {
                    return false;
                }
            }
        }
        return super.shouldApplyMixin(targetClassName, mixinClassName);
    }
}
