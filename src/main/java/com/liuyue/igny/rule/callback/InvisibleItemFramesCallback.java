package com.liuyue.igny.rule.callback;

import carpet.api.settings.CarpetRule;
import com.liuyue.igny.utils.interfaces.invisibleItemFrames.ItemFrameRefreshable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;

public class InvisibleItemFramesCallback implements RuleCallback<String> {
    @Override
    public void onChange(CommandSourceStack source, CarpetRule<String> rule, String oldValue, String newValue) {
        MinecraftServer server = source.getServer();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ItemFrame) {
                    ((ItemFrameRefreshable) entity).igny$refreshInvisible(newValue);
                }
            }
        }
    }
}
