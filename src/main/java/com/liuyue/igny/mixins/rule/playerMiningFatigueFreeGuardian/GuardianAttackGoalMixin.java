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

//#if MC >= 12103
//$$import net.minecraft.server.level.ServerLevel;
//#endif

@Mixin(targets = "net.minecraft.world.entity.monster.Guardian$GuardianAttackGoal")
public class GuardianAttackGoalMixin {
    @Shadow @Final private Guardian guardian;

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 12103
                    //$$ target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"
                    //#else
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
                    //#endif
            )
    )
    private boolean hurt(
            //#if MC >= 12103
            //$$ LivingEntity target, ServerLevel level, DamageSource originalSource, float amount, Operation<Boolean> original
            //#else
            LivingEntity target, DamageSource originalSource, float amount, Operation<Boolean> original
            //#endif
    ) {
        if (IGNYSettings.PLAYER_MINING_FATIGUE_FREE_GUARDIAN.value()) {
            DamageSource newSource = target.damageSources().mobAttack(guardian);
            //#if MC >= 12103
            //$$ return original.call(target, level, newSource, amount);
            //#else
            return original.call(target, newSource, amount);
            //#endif
        }
        //#if MC >= 12103
        //$$ return original.call(target, level, originalSource, amount);
        //#else
        return original.call(target, originalSource, amount);
        //#endif
    }
}