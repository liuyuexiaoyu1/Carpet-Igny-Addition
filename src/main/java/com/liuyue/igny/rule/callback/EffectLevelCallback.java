package com.liuyue.igny.rule.callback;

import carpet.api.settings.CarpetRule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class EffectLevelCallback implements RuleCallback<Boolean> {
    @Override
    public void onChange(CommandSourceStack source, CarpetRule<Boolean> rule, Boolean oldValue, String newValue) {
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            source.getServer().getCommands().sendCommands(player);
        }
    }
}
