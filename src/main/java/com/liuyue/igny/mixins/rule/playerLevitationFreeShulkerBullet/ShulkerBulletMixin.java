package com.liuyue.igny.mixins.rule.playerLevitationFreeShulkerBullet;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShulkerBullet.class)
public class ShulkerBulletMixin {

    @Redirect(
            method = "onHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"

            )
    )
    private boolean onHitEntity(LivingEntity target, MobEffectInstance effect, Entity source) {
        if (IGNYSettings.playerLevitationFreeShulkerBullet){
        if (target instanceof Player) {
            return false;
        }
        }
        return target.addEffect(effect, source);

    }
}