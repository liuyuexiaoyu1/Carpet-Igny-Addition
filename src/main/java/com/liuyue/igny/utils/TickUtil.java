package com.liuyue.igny.utils;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class TickUtil {
    public static boolean shouldSprint(MinecraftServer server) {
        switch (IGNYSettings.betterSprintGameTick) {
            case "false" -> {
                return true;
            }
            case "playerJoin" -> {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!(player instanceof EntityPlayerMPFake) && !IGNYSettings.sprintWhitelistPlayers.contains(player.getUUID())) {
                        return false;
                    }
                }
                return true;
            }
            case "true" -> {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!(player instanceof EntityPlayerMPFake)) return false;
                }
                return true;
            }
        }
        return true;
    }
}
