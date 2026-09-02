package com.liuyue.igny.rule.validators;

import carpet.utils.Translations;
import com.liuyue.igny.rule.RuleAccessor;
import com.liuyue.igny.rule.ValueValidator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PrerequisiteRuleValidator {
    @SafeVarargs
    public static <T> ValueValidator<T> createValidator(RuleAccessor<T> rule, T... needValues) {
        Set<T> allowedValues = new HashSet<>(Arrays.asList(needValues));
        return new ValueValidator<>() {
            @Override
            public boolean validate(T newValue) {
                return allowedValues.contains(rule.value());
            }

            @Override
            public boolean shouldSendDetail() {
                return false;
            }

            @Override
            public Component errorMessage() {
                return Component.literal(rule.name() + " " + Translations.tr("igny.prerequisite_rule")).withStyle(ChatFormatting.RED);
            }
        };
    }

    @SafeVarargs
    public static <T> ValueValidator<T> createValidatorWithBreak(RuleAccessor<T> rule, T... breakValues) {
        Set<T> allowedValues = new HashSet<>(Arrays.asList(breakValues));
        return new ValueValidator<>() {
            @Override
            public boolean validate(T newValue) {
                return !allowedValues.contains(rule.value());
            }

            @Override
            public boolean shouldSendDetail() {
                return false;
            }

            @Override
            public Component errorMessage() {
                return Component.literal(rule.name() + " " + Translations.tr("igny.prerequisite_rule")).withStyle(ChatFormatting.RED);
            }
        };
    }
}
