package com.liuyue.igny.rule.validators;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CrammingEntityValidator extends Validator<String> {
    @Override
    public String validate(CommandSourceStack source, CarpetRule<String> rule, String newValue, String string) {
        if (newValue == null || newValue.equals("#none")) {
                IGNYSettings.CRAMMING_ENTITIES.clear();
            return "#none";
        }
        if (source != null) {
            var registry = source.getServer().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            List<String> names = Arrays.stream(newValue.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            for (String name : names) {
                if (!isValidEntityName(registry, name)) {
                    source.sendFailure(Component.translatable("igny.settings.failure.unknown_entity", name));
                    return null;
                }
            }
            IGNYSettings.CRAMMING_ENTITIES = new HashSet<>(names);
            return newValue;
        }
        return null;
    }

    private boolean isValidEntityName(Registry<EntityType<?>> registry, String name) {
        try {
            ResourceLocation id = ResourceLocation.tryParse(name);
            return id != null && registry.containsKey(id);
        } catch (Exception e) {
            return false;
        }
    }
}
