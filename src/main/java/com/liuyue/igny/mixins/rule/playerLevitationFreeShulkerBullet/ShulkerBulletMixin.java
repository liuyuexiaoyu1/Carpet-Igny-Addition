package com.liuyue.igny.mixins.rule.playerLevitationFreeShulkerBullet;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShulkerBullet.class)
public class ShulkerBulletMixin {
    @WrapOperation(
            method = "onHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean onHitEntity(LivingEntity target, MobEffectInstance effect, Entity source, Operation<Boolean> original) {
        if (IGNYSettings.PLAYER_LEVITATION_FREE_SHULKER_BULLET.value() && target instanceof Player){
            return false;
        }
        return original.call(target, effect, source);
    }
}