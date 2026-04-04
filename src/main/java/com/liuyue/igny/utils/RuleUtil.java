package com.liuyue.igny.utils;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.Objects;

public class RuleUtil {
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

    public static Boolean canEntityIDSuppression(ServerPlayer player) {
        if ("false".equalsIgnoreCase(IGNYSettings.simpleEntityIDSuppression)) {
            return false;
        }
        //#if MC >= 12110
        //$$ String name = player.getGameProfile().name();
        //#else
        String name = player.getGameProfile().getName();
        //#endif
        if ("true".equalsIgnoreCase(IGNYSettings.simpleEntityIDSuppression)) {
            return "eIDSuppression".equalsIgnoreCase(name);
        }

        return Objects.equals(IGNYSettings.simpleEntityIDSuppression.toLowerCase(), name.toLowerCase());
    }

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
