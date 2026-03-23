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
//#if MC >= 12102
//$$ import net.minecraft.server.level.ServerLevel;
//#endif

import java.util.Optional;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    //#if MC >= 12102
    //$$ @Inject(method = "isWearingSafeArmor", at = @At(value = "HEAD"), cancellable = true)
    //#else
    @Inject(method = "isWearingGold", at = @At(value = "HEAD"), cancellable = true)
    //#endif
    private static void isWearingGold(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.piglinNeutralBehavior) cir.setReturnValue(true);
    }

    @Inject(method = "angerNearbyPiglins", at = @At(value = "HEAD"), cancellable = true)
    //#if MC >= 12102
    //$$ private static void angerNearbyPiglins(ServerLevel level, Player player, boolean angerOnlyIfCanSee, CallbackInfo ci) {
    //#else
    private static void angerNearbyPiglins(Player player, boolean angerOnlyIfCanSee, CallbackInfo ci) {
        //#endif
        if (IGNYSettings.piglinNeutralBehavior) ci.cancel();
    }

    @Inject(method = "findNearestValidAttackTarget", at = @At(value = "HEAD"), cancellable = true)
    //#if MC >= 12102
    //$$ private static void findNearestValidAttackTarget(ServerLevel level, Piglin piglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
    //#else
    private static void findNearestValidAttackTarget(Piglin piglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        //#endif
        if (IGNYSettings.piglinNeutralBehavior) cir.setReturnValue(Optional.empty());
    }
}
