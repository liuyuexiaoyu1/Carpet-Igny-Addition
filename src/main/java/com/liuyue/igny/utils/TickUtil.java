package com.liuyue.igny.utils;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
//#if MC >= 12003
import net.minecraft.world.TickRateManager;
//#elseif MC > 11904
//$$ import carpet.helpers.TickRateManager;
//$$ import carpet.fakes.MinecraftServerInterface;
//#else
//$$ import carpet.helpers.TickSpeed;
//#endif

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

    public static void checkTickRate(MinecraftServer server) {
        if (server != null) {
            //#if MC >= 12003
            TickRateManager manager = server.tickRateManager();
            //#elseif MC > 11904
            //$$ TickRateManager manager = ((MinecraftServerInterface)server).getTickRateManager();
            //#endif
            if (!IGNYSettings.betterSprintGameTick.equals("false")) {
                if (!TickUtil.shouldSprint(server)) {
                    //#if MC <= 11904
                    //$$ TickSpeed.tickrate(20);
                    //#else
                    manager.setTickRate(20);
                    //#endif
                }
                return;
            }
            //#if MC <= 11904
            //$$ if (TickUtil.shouldSprint(server) && TickSpeed.tickrate != IGNYSettings.originalTPS) {
            //#else
            if (TickUtil.shouldSprint(server) && manager.tickrate() != IGNYSettings.originalTPS) {
                //#endif
                //#if MC <= 11904
                //$$ TickSpeed.tickrate(IGNYSettings.originalTPS);
                //#else
                manager.setTickRate(IGNYSettings.originalTPS);
                //#endif
            }
        }
    }
}
