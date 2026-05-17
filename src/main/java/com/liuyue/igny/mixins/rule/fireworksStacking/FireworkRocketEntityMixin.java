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
            int totalCount = peers.size();
            int effectiveCount = (maxStack <= 0) ? totalCount : Math.min(totalCount, maxStack);
            double dynamicLimit = 1.5 + (effectiveCount - 1) * 1.0;
            double countWeight = 1.0 / totalCount;
            Vec3 look = player.getLookAngle();
            Vec3 currentVel = player.getDeltaMovement();
            double deltaX = (look.x * 0.1 + (look.x * dynamicLimit - currentVel.x) * 0.5) * countWeight;
            double deltaY = (look.y * 0.1 + (look.y * dynamicLimit - currentVel.y) * 0.5) * countWeight;
            double deltaZ = (look.z * 0.1 + (look.z * dynamicLimit - currentVel.z) * 0.5) * countWeight;
            original.call(instance, currentVel.add(deltaX, deltaY, deltaZ));
            return;
        }
        original.call(instance, vec3);
    }
}
