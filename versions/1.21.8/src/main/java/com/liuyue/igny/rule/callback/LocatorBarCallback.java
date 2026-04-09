package com.liuyue.igny.rule.callback;

import carpet.api.settings.CarpetRule;
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.waypoints.ServerWaypointManager;

public class LocatorBarCallback implements RuleCallback<Boolean> {
    @Override
    public void onChange(CommandSourceStack source, CarpetRule<Boolean> rule, Boolean oldValue, String newValue) {
        MinecraftServer server = source.getServer();
        PlayerList list = server.getPlayerList();
        for (ServerPlayer player : list.getPlayers()) {
            if (!(player instanceof EntityPlayerMPFake)) continue;
            ServerWaypointManager manager = player.level().getWaypointManager();
            if (rule.value()) {
                manager.removePlayer(player);
            } else {
                manager.addPlayer(player);
            }
        }
    }
}
