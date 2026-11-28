package com.liuyue.igny.mixins.rule.piglinNeutralBehavior;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?>= 1.21.2 ? import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    @Inject(method = "isWearingGold", at = @At(value = "HEAD"), cancellable = true) //#replace >= 1.21.2 ? @Inject(method = "isWearingSafeArmor", at = @At(value = "HEAD"), cancellable = true)
    private static void isWearingGold(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.PIGLIN_NEUTRAL_BEHAVIOR.value()) cir.setReturnValue(true);
    }

    @Inject(method = "angerNearbyPiglins", at = @At(value = "HEAD"), cancellable = true)
    private static void angerNearbyPiglins(Player player, boolean angerOnlyIfCanSee, CallbackInfo ci) { //#replace >= 1.21.2 ? private static void angerNearbyPiglins(ServerLevel level, Player player, boolean angerOnlyIfCanSee, CallbackInfo ci) {
        if (IGNYSettings.PIGLIN_NEUTRAL_BEHAVIOR.value()) ci.cancel();
    }

    @WrapMethod(method = "findNearestValidAttackTarget")
    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Piglin piglin, Operation<Optional<? extends LivingEntity>> original) { //#replace >= 1.21.2 ? private static Optional<? extends LivingEntity> findNearestValidAttackTarget(ServerLevel level, Piglin piglin, Operation<Optional<? extends LivingEntity>> original) {
        Optional<? extends LivingEntity> result = original.call(piglin); //#replace >= 1.21.2 ? Optional<? extends LivingEntity> result = original.call(level, piglin);
        if (!IGNYSettings.PIGLIN_NEUTRAL_BEHAVIOR.value()) {
            return result;
        }
        return result.filter(target -> !(target instanceof Player));
    }
}
