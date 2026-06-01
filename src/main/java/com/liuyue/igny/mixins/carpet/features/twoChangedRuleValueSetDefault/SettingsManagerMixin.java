package com.liuyue.igny.mixins.carpet.features.twoChangedRuleValueSetDefault;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

    @Unique private boolean canceled = false;

    @WrapOperation(method = "setRule", at = @At(value = "INVOKE", target = "Lcarpet/api/settings/CarpetRule;set(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private <T> void onSetRule(CarpetRule<T> instance, CommandSourceStack commandSourceStack, String s, Operation<Void> original){
        if (IGNYSettings.TWO_CHANGED_RULE_VALUE_SET_DEFAULT.value()){
            T originValue = instance.value();
            original.call(instance, commandSourceStack, s);
            if (originValue.equals(instance.value())){
                setDefault(commandSourceStack, instance, s);
                canceled = true;
            }
            return;
        }
        original.call(instance, commandSourceStack, s);
    }

    @Inject(method = "setRule", at = @At(value = "INVOKE", target = "Lcarpet/api/settings/CarpetRule;set(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V", shift = At.Shift.AFTER), cancellable = true)
    private void setCanceled(CommandSourceStack source, CarpetRule<?> rule, String newValue, CallbackInfoReturnable<Integer> cir) {
        if (canceled) {
            cir.setReturnValue(1);
            canceled = false;
        }
    }
}
