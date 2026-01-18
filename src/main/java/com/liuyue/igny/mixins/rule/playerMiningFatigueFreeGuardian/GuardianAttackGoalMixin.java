package com.liuyue.igny.mixins.rule.playerMiningFatigueFreeGuardian;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC >= 12103
//$$import net.minecraft.server.level.ServerLevel;
//#endif

@Mixin(targets = "net.minecraft.world.entity.monster.Guardian$GuardianAttackGoal")
public class GuardianAttackGoalMixin {

    @Shadow @Final private Guardian guardian;

    @Redirect(
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
    private boolean redirectAttack(
            //#if MC >= 12103
            //$$ LivingEntity target, ServerLevel level, DamageSource originalSource, float amount
            //#else
            LivingEntity target, DamageSource originalSource, float amount
            //#endif
    ) {
        if (IGNYSettings.playerMiningFatigueFreeGuardian) {
            DamageSource newSource = target.damageSources().mobAttack(guardian);
            //#if MC >= 12103
            //$$ return target.hurtServer(level, newSource, amount);
            //#else
            return target.hurt(newSource, amount);
            //#endif
        }
        //#if MC >= 12103
        //$$ return target.hurtServer(level, originalSource, amount);
        //#else
        return target.hurt(originalSource, amount);
        //#endif
    }
}