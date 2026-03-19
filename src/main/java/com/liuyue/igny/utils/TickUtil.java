package com.liuyue.igny.utils;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class TickUtil {
    public static boolean shouldSprint(MinecraftServer server) {
        if (!IGNYSettings.betterSprintGameTick) return true;
        boolean bl = true;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!(player instanceof EntityPlayerMPFake)) {
                bl = false;
                break;
            }
        }
        return bl;
    }
}
