package com.liuyue.igny.mixins.rule.playerMiningFatigueFreeGuardian;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

//?>= 1.21.3 ? import net.minecraft.server.level.ServerLevel;

@Mixin(targets = "net.minecraft.world.entity.monster.Guardian$GuardianAttackGoal")
public class GuardianAttackGoalMixin {
    @Shadow @Final private Guardian guardian;

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z" //#replace >= 1.21.3 ? target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean hurt(
            LivingEntity target, DamageSource originalSource, float amount, Operation<Boolean> original //#replace >= 1.21.3 ? LivingEntity target, ServerLevel level, DamageSource originalSource, float amount, Operation<Boolean> original
    ) {
        if (IGNYSettings.PLAYER_MINING_FATIGUE_FREE_GUARDIAN.value()) {
            DamageSource newSource = target.damageSources().mobAttack(guardian);
            return original.call(target, newSource, amount); //#replace >= 1.21.3 ? return original.call(target, level, newSource, amount);
        }
        return original.call(target, originalSource, amount); //#replace >= 1.21.3 ? return original.call(target, level, originalSource, amount);
    }
}