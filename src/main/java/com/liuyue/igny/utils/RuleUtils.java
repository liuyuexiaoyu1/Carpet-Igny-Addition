package com.liuyue.igny.utils;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.IGNYSettings;

import java.util.Objects;

public class RuleUtils {
    //#if MC >= 12005
    public static Boolean canSoundSuppression(String name) {
        if ("false".equalsIgnoreCase(IGNYSettings.simpleSoundSuppression)) {
            return false;
        }
        if (name == null) {
            return false;
        }
        if ("true".equalsIgnoreCase(IGNYSettings.simpleSoundSuppression)) {
            return "声音抑制器".equals(name) || "soundSuppression".equalsIgnoreCase(name);
        }

        return Objects.equals(IGNYSettings.simpleSoundSuppression.toLowerCase(), name.toLowerCase());
    }
    //#endif

    public static Object getCarpetRulesValue(String modId, String ruleName) {
        if(IGNYServerMod.CARPET_ADDITION_MOD_IDS.contains(modId)){
            CarpetRule<?> carpetRule = CarpetServer.settingsManager.getCarpetRule(ruleName);
            if (carpetRule == null) {
                return false;
            }
            return carpetRule.value() == null ? false : carpetRule.value();
        }
        return false;
    }

}
