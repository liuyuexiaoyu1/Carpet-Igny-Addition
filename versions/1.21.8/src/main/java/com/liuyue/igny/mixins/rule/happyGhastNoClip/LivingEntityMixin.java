package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.HappyGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapOperation(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;dismountsUnderwater()Z"))
    private boolean dismountsUnderwater(Entity instance, Operation<Boolean> original) {
        if (instance instanceof HappyGhast && instance.isVehicle() && IGNYSettings.happyGhastNoClip) {
            return false;
        }
        return original.call(instance);
    }

    @WrapOperation(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 2))
    private boolean hurtServer(LivingEntity instance, ServerLevel serverLevel, DamageSource damageSource, float v, Operation<Boolean> original) {
        if (instance instanceof HappyGhast && instance.isVehicle() && IGNYSettings.happyGhastNoClip) {
            return false;
        }
        return original.call(instance, serverLevel, damageSource, v);
    }
}
