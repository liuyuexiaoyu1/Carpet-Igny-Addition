package com.liuyue.igny.rule.validators;

import carpet.utils.Translations;
import com.liuyue.igny.rule.ValueValidator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

public class ModValidator {
    public static <T> ValueValidator<T> createValidator(String modId, String modName) {
        return new ValueValidator<>() {
            @Override
            public boolean validate(T newValue) {
                return FabricLoader.getInstance().isModLoaded(modId);
            }
            @Override
            public Component errorMessage() {
                return Component.literal(modName + " " + Translations.tr("igny.mod_not_found"));
            }
        };
    }
}
