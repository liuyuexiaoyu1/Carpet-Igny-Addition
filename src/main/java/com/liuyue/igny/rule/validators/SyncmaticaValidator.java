package com.liuyue.igny.rule.validators;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import carpet.utils.Translations;
import com.liuyue.igny.rule.CommandPermissionLevel;
import com.liuyue.igny.rule.ValueValidator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SyncmaticaValidator extends Validator<CommandPermissionLevel> {
    @Override
    public CommandPermissionLevel validate(CommandSourceStack source, CarpetRule<CommandPermissionLevel> rule, CommandPermissionLevel newValue, String string) {
        if (source != null && source.getEntity() instanceof ServerPlayer) {
            if (!FabricLoader.getInstance().isModLoaded("syncmatica")) {
                source.sendFailure(Component.literal(Translations.tr("igny.syncmatica_not_found")));
                return null;
            }
        }
        return newValue;
    }

    public static ValueValidator<CommandPermissionLevel> createValidator() {
        return new ValueValidator<>() {
            @Override
            public boolean validate(CommandPermissionLevel newValue) {
                return FabricLoader.getInstance().isModLoaded("syncmatica");
            }

            @Override
            public Component errorMessage() {
                return Component.literal(Translations.tr("igny.syncmatica_not_found"));
            }
        };
    }
}
