package com.liuyue.igny.mixins.rule.fireworksStacking;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    private void handleGroupMovement(LivingEntity instance, Vec3 vec3, Operation<Void> original) {
        if (IGNYSettings.fireworksStacking != 1) {
            if (!(instance instanceof Player player) || !player.isFallFlying()) {
                original.call(instance, vec3);
                return;
            }
            List<FireworkRocketEntity> peers = instance.level().getEntitiesOfClass(
                    FireworkRocketEntity.class,
                    instance.getBoundingBox().inflate(2.0),
                    rocket -> ((FireworkRocketEntityAccessor) rocket).getAttachedToEntity() == player
            );
            if (peers.isEmpty()) {
                original.call(instance, vec3);
                return;
            }
            int maxStack = IGNYSettings.fireworksStacking;
            int totalCount = peers.size() + 1;
            int effectiveStacks = (maxStack <= 0) ? totalCount : Math.min(totalCount, maxStack);
            Vec3 lookAngle = player.getLookAngle();
            Vec3 vec32 = player.getDeltaMovement();
            double boostLimit = 1.5 + (effectiveStacks + 1);
            Vec3 stackedVel = vec32.add(
                    lookAngle.x * 0.1 + (lookAngle.x * boostLimit - vec32.x) * 0.5,
                    lookAngle.y * 0.1 + (lookAngle.y * boostLimit - vec32.y) * 0.5,
                    lookAngle.z * 0.1 + (lookAngle.z * boostLimit - vec32.z) * 0.5
            );
            original.call(instance, stackedVel);
            return;
        }
        original.call(instance, vec3);
    }
}
