package com.liuyue.igny.mixins.carpet.features.twoChangedRuleValueSetDefault;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SettingsManager.class)
public abstract class SettingsManagerMixin {
    @Shadow
    protected abstract int setDefault(CommandSourceStack source, CarpetRule<?> rule, String stringValue);

    @Inject(method = "setRule", at = @At(value = "INVOKE", target = "Lcarpet/api/settings/CarpetRule;set(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"), cancellable = true)
    private void onSetRule(CommandSourceStack source, CarpetRule<?> rule, String newValue, CallbackInfoReturnable<Integer> cir){
        if (IGNYSettings.twoChangedRuleValueSetDefault && rule.value().toString().equals(newValue)){
            setDefault(source, rule, rule.value().toString());
            cir.setReturnValue(1);
        }
    }
}
