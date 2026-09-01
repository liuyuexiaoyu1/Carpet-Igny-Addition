package com.liuyue.igny.rule.validators;

import carpet.utils.Translations;
import com.liuyue.igny.rule.RuleAccessor;
import com.liuyue.igny.rule.ValueValidator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

public class PrerequisiteRuleValidator {
    public static <T> ValueValidator<T> createValidator(RuleAccessor<?> rule, Object needValue) {
        return new ValueValidator<>() {
            @Override
            public boolean validate(T newValue) {
                return rule.value().equals(needValue);
            }
            @Override
            public Component errorMessage() {
                return Component.literal(rule.name() + " " + Translations.tr("igny.prerequisite_rule"));
            }
        };
    }
}
