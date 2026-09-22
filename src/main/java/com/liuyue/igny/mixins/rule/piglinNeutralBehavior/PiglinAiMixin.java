package com.liuyue.igny.mixins.rule.piglinNeutralBehavior;

import com.liuyue.igny.IGNYSettings;
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

    @Inject(method = "findNearestValidAttackTarget", at = @At(value = "HEAD"), cancellable = true)
    private static void findNearestValidAttackTarget(Piglin piglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) { //#replace >= 1.21.2 ? private static void findNearestValidAttackTarget(ServerLevel level, Piglin piglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        if (IGNYSettings.PIGLIN_NEUTRAL_BEHAVIOR.value()) cir.setReturnValue(Optional.empty());
    }
}
