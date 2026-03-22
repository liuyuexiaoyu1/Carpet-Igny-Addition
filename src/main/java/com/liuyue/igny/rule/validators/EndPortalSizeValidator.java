package com.liuyue.igny.rule.validators;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import carpet.utils.Translations;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class EndPortalSizeValidator extends Validator<Integer> {
    @Override
    public Integer validate(CommandSourceStack source, CarpetRule<Integer> rule, Integer newValue, String string) {
        if (source != null && source.getEntity() instanceof ServerPlayer) {
            if (newValue > 516) {
                source.sendFailure(Component.literal(Translations.tr("carpet.rule.maxEndPortalSize.failure")));
                return null;
            }
        }
        return newValue;
    }
}
