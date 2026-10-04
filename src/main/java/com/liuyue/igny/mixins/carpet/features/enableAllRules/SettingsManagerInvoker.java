package com.liuyue.igny.mixins.carpet.features.enableAllRules;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SettingsManager.class)
public interface SettingsManagerInvoker {
    @Invoker("setRule")
    int invokeSetRule(CommandSourceStack source, CarpetRule<?> rule, String newValue);
}
