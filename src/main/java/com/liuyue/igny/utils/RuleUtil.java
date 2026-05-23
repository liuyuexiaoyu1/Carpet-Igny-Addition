package com.liuyue.igny.utils;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Objects;

public class RuleUtil {
    //#if MC >= 12005
    public static Boolean canSoundSuppression(String name) {
        if ("false".equalsIgnoreCase(IGNYSettings.SIMPLE_SOUND_SUPPRESSION.value())) {
            return false;
        }
        if (name == null) {
            return false;
        }
        if ("true".equalsIgnoreCase(IGNYSettings.SIMPLE_SOUND_SUPPRESSION.value())) {
            return "声音抑制器".equals(name) || "soundSuppression".equalsIgnoreCase(name);
        }

        return Objects.equals(IGNYSettings.SIMPLE_SOUND_SUPPRESSION.value().toLowerCase(), name.toLowerCase());
    }
    //#endif

    public static Boolean canEntityIDSuppression(ServerPlayer player) {
        if ("false".equalsIgnoreCase(IGNYSettings.SIMPLE_ENTITY_ID_SUPPRESSION.value())) {
            return false;
        }
        //#if MC >= 12110
        //$$ String name = player.getGameProfile().name();
        //#else
        String name = player.getGameProfile().getName();
        //#endif
        if ("true".equalsIgnoreCase(IGNYSettings.SIMPLE_ENTITY_ID_SUPPRESSION.value())) {
            return "eIDSuppression".equalsIgnoreCase(name);
        }

        return Objects.equals(IGNYSettings.SIMPLE_ENTITY_ID_SUPPRESSION.value().toLowerCase(), name.toLowerCase());
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

    public static void removeVehicle(ServerPlayer serverPlayer) {
        if (!Objects.equals(IGNYSettings.KILL_FAKE_PLAYER_REMOVE_VEHICLE.value(), "true")) {
            boolean shouldKeep = true;
            if (Objects.equals(IGNYSettings.KILL_FAKE_PLAYER_REMOVE_VEHICLE.value(), "canBoatTrade")) {
                if (serverPlayer.getVehicle() != null) {
                    shouldKeep = serverPlayer.getVehicle().getPassengers().stream().noneMatch(entity -> entity instanceof Villager || entity instanceof WanderingTrader);
                }
            }
            if (shouldKeep) {
                serverPlayer.stopRiding();
            }
        }
    }

    public static boolean isNightmarishBlock(Block block) {
        return block.equals(Blocks.BUDDING_AMETHYST)
                //#if MC >= 12005
                || block.equals(Blocks.TRIAL_SPAWNER)
                || block.equals(Blocks.VAULT)
                //#elseif MC >= 12003
                //$$ || block.equals(Blocks.TRIAL_SPAWNER)
                //#endif
                ;
    }
}
