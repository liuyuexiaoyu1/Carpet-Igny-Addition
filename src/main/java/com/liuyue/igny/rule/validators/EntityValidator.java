package com.liuyue.igny.rule.validators;

import com.liuyue.igny.rule.ValueValidator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class EntityValidator {

    public static ValueValidator<String> createValidator() {
        return new ValueValidator<>() {
            @Override
            public boolean validate(String newValue) {
                return isValidValue(newValue);
            }

            @Override
            public boolean shouldSendDetail() {
                return false;
            }

            @Override
            public Component errorMessage() {
                return Component.translatable("igny.settings.failure.unknown_entity").withStyle(ChatFormatting.RED);
            }
        };
    }

    private static boolean isValidValue(String value) {
        if (value == null || value.equals("#none") || value.startsWith("#")) {
            return true;
        }
        return parseEntityNames(value).stream().allMatch(EntityValidator::isValidEntityType);
    }

    public static void applyChange(String value, Set<String> targetSet) {
        if (value == null || value.equals("#none")) {
            targetSet.clear();
            return;
        }
        if (value.startsWith("#")) {
            targetSet.clear();
            targetSet.add(value);
            return;
        }
        List<String> names = parseEntityNames(value);
        targetSet.clear();
        targetSet.addAll(names);
    }

    private static List<String> parseEntityNames(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static boolean isValidEntityType(String name) {
        ResourceLocation id = ResourceLocation.tryParse(name);
        return id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id);
    }
}