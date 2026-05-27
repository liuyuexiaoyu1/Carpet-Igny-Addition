package com.liuyue.igny.mixins.rule.allowInvalidBeaconEffect;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {
    @Inject(method = "validateEffects", at = @At(value = "HEAD"), cancellable = true)
    private static void validateEffects(Holder<MobEffect> primary, Holder<MobEffect> secondary, int levels, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.ALLOW_INVALID_BEACON_EFFECT.value()) {
            cir.setReturnValue(true);
        }
    }
}
