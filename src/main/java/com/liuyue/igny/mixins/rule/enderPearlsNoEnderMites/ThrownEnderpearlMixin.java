package com.liuyue.igny.mixins.rule.enderPearlsNoEnderMites;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderpearlMixin {
    @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextFloat()F"))
    private float create(RandomSource instance, Operation<Float> original) {
        return IGNYSettings.ENDER_PEARLS_NO_ENDER_MITES.value() ? 1 : original.call(instance);
    }
}
