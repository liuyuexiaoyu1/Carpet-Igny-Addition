package com.liuyue.igny.mixins.carpet.features.twoChangedRuleValueSetDefault;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import carpet.settings.ParsedRule;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SettingsManager.class)
public abstract class SettingsManagerMixin {
    @Shadow
    protected abstract int setDefault(CommandSourceStack source, CarpetRule<?> rule, String stringValue);

    @Unique private Object rawValue;

    @Inject(method = "setRule", at= @At(value = "INVOKE", target = "Lcarpet/api/settings/CarpetRule;set(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private <T> void onSet(CommandSourceStack source, CarpetRule<T> rule, String newValue, CallbackInfoReturnable<Integer> cir){
        rawValue = rule.value();
    }

    @SuppressWarnings("removal")
    @Inject(method = "setRule", at = @At(value = "INVOKE", target = "Lcarpet/utils/Messenger;m(Lnet/minecraft/commands/CommandSourceStack;[Ljava/lang/Object;)V"), cancellable = true)
    private <T> void onSetRule(CommandSourceStack source, CarpetRule<T> rule, String newValue, CallbackInfoReturnable<Integer> cir){
        if (IGNYSettings.twoChangedRuleValueSetDefault){
            T value = rule.value();
            if (rule instanceof ParsedRule<T> parsedRule) {
                for (carpet.api.settings.Validator<T> validator : parsedRule.realValidators) {
                    value = validator.validate(source, rule, rule.value(), newValue);
                }
            }
            if (rawValue == value) {
                setDefault(source, rule, rule.value().toString());
                cir.setReturnValue(1);
            }
        }
    }
}
