package com.liuyue.igny.rule.validators;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.rule.ValueValidator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class EntityValidator extends Validator<String> {
    @Override
    public String validate(CommandSourceStack source, CarpetRule<String> rule, String newValue, String string) {
        return validateEntityList(newValue, rule.name(), source);
    }

    private static String validateEntityList(String newValue, String ruleName, CommandSourceStack source) {
        if (newValue == null || newValue.equals("#none")) {
            clearSets(ruleName);
            return newValue;
        }
        if (newValue.startsWith("#")) {
            updateSets(ruleName, List.of(newValue));
            return newValue;
        }
        List<String> names = Arrays.stream(newValue.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        for (String name : names) {
            ResourceLocation id = ResourceLocation.tryParse(name);
            if (id == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                if (source != null) {
                    source.sendFailure(Component.translatable("igny.settings.failure.unknown_entity", name));
                }
                return null;
            }
        }
        updateSets(ruleName, names);
        return newValue;
    }

    private static void clearSets(String ruleName) {
        if (ruleName.equals("optimizedEntityList")) {
            IGNYSettings.CRAMMING_ENTITIES.clear();
        } else if (ruleName.equals("entityIDSuppressionWhitelist")) {
            IGNYSettings.EIDWhitelist.clear();
        }
    }

    private static void updateSets(String ruleName, List<String> names) {
        if (ruleName.equals("optimizedEntityList")) {
            IGNYSettings.CRAMMING_ENTITIES = new HashSet<>(names);
        } else if (ruleName.equals("entityIDSuppressionWhitelist")) {
            IGNYSettings.EIDWhitelist = new HashSet<>(names);
        }
    }

    public static ValueValidator<String> createOptimizedEntityValidator() {
        return new ValueValidator<>() {
            @Override
            public boolean validate(String newValue) {
                if (newValue == null || newValue.equals("#none") || newValue.startsWith("#")) return true;
                return Arrays.stream(newValue.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .allMatch(name -> {
                            ResourceLocation id = ResourceLocation.tryParse(name);
                            return id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id);
                        });
            }

            @Override
            public Component errorMessage() {
                return Component.translatable("igny.settings.failure.unknown_entity");
            }
        };
    }

    public static ValueValidator<String> createEntityIDSuppressionValidator() {
        return new ValueValidator<>() {
            @Override
            public boolean validate(String newValue) {
                if (newValue == null || newValue.equals("#none") || newValue.startsWith("#")) return true;
                return Arrays.stream(newValue.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .allMatch(name -> {
                            ResourceLocation id = ResourceLocation.tryParse(name);
                            return id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id);
                        });
            }

            @Override
            public Component errorMessage() {
                return Component.translatable("igny.settings.failure.unknown_entity");
            }
        };
    }

    public static void onOptimizedEntityListChanged(CommandSourceStack source, String value) {
        if (value == null || value.equals("#none")) {
            IGNYSettings.CRAMMING_ENTITIES.clear();
            return;
        }
        if (value.startsWith("#")) {
            IGNYSettings.CRAMMING_ENTITIES = new HashSet<>(List.of(value));
            return;
        }
        List<String> names = Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        IGNYSettings.CRAMMING_ENTITIES = new HashSet<>(names);
    }

    public static void onEntityIDWhitelistChanged(CommandSourceStack source, String value) {
        if (value == null || value.equals("#none")) {
            IGNYSettings.EIDWhitelist.clear();
            return;
        }
        if (value.startsWith("#")) {
            IGNYSettings.EIDWhitelist = new HashSet<>(List.of(value));
            return;
        }
        List<String> names = Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        IGNYSettings.EIDWhitelist = new HashSet<>(names);
    }
}
