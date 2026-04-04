package com.liuyue.igny.mixins.rule.superEffectLevel;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin {
    //#if MC >= 12005
    @WrapOperation(method = "<init>(Lnet/minecraft/core/Holder;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I"))
    private int init(int value, int min, int max, Operation<Integer> original, @Local(ordinal = 1, argsOnly = true) int amplifier) {
        if (IGNYSettings.superEffectLevel) {
            return amplifier;
        }
        return original.call(value, min, max);
    }
    //#endif
}
