package com.liuyue.igny.mixins.carpet.features.showRuleSource;

import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.ClassUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(SettingsManager.class)
public class SettingsManagerMixin {
    @Inject(method = "parseSettingsClass", at = @At(value = "TAIL"))
    private static void parseSettingsClass(Class<?> settingsClass, CallbackInfo ci) {
        ClassUtil.getModIdFromClassAsync(settingsClass, modId ->
                Arrays.stream(settingsClass.getDeclaredFields()).forEach(field -> {
                    String ruleName = field.getName();
                    if (CarpetServer.settingsManager.getCarpetRule(ruleName) != null) {
                        IGNYSettings.modRuleTree
                        .computeIfAbsent(modId, k -> new ArrayList<>())
                        .add(ruleName);
                    }
                })
        );
    }
}
