package com.liuyue.igny.utils;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
//#if >= 1.20.3
import net.minecraft.world.TickRateManager;
//#elseif > 1.19.4
/*$$import carpet.helpers.TickRateManager;
import carpet.fakes.MinecraftServerInterface;$$*/
//#else
//$$ import carpet.helpers.TickSpeed;
//#endif

public class TickUtil {
    private static boolean lastSprint = true;

    public static boolean shouldSprint(MinecraftServer server) {
        switch (IGNYSettings.BETTER_SPRINT_GAME_TICK.value()) {
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

    public static void checkTickRate(MinecraftServer server) {
        if (server != null) {
            //#if >= 1.20.3
            TickRateManager manager = server.tickRateManager();
            //#elseif > 1.19.4
            //$$ TickRateManager manager = ((MinecraftServerInterface)server).getTickRateManager();
            //#endif
            if (!IGNYSettings.BETTER_SPRINT_GAME_TICK.value().equals("false")) {
                boolean sprint = TickUtil.shouldSprint(server);

                if (sprint != lastSprint) {
                    lastSprint = sprint;

                    if (!sprint) {
                        manager.setTickRate(20); //#replace <= 1.19.4 ? TickSpeed.tickrate(20);
                    }
                }
                return;
            }
            if (TickUtil.shouldSprint(server) && manager.tickrate() != IGNYSettings.originalTPS) { //#replace <= 1.19.4 ? if (TickUtil.shouldSprint(server) && TickSpeed.tickrate != IGNYSettings.originalTPS) {
                manager.setTickRate(IGNYSettings.originalTPS); //#replace <= 1.19.4 ? TickSpeed.tickrate(IGNYSettings.originalTPS);
            }
        }
    }
}
