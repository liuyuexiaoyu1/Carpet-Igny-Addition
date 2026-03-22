package com.liuyue.igny.rule.validators;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import carpet.utils.Translations;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SyncmaticaValidator extends Validator<String> {
    @Override
    public String validate(CommandSourceStack source, CarpetRule<String> rule, String newValue, String string) {
        if (source != null && source.getEntity() instanceof ServerPlayer) {
            if (!FabricLoader.getInstance().isModLoaded("syncmatica")) {
                source.sendFailure(Component.literal(Translations.tr("igny.syncmatica_not_found")));
                return null;
            }
        }
        return newValue;
    }
}