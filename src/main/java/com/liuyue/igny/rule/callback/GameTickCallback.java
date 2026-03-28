package com.liuyue.igny.rule.callback;

import carpet.api.settings.CarpetRule;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.utils.TickUtil;
import net.minecraft.commands.CommandSourceStack;

public class GameTickCallback implements RuleCallback<String> {
    @Override
    public void onChange(CommandSourceStack source, CarpetRule<String> rule, String oldValue, String newValue) {
        TickUtil.checkTickRate(IGNYServer.getInstance().getMinecraftServer());
    }
}
