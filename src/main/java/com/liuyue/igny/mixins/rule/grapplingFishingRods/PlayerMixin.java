package com.liuyue.igny.mixins.rule.grapplingFishingRods;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.grapplingFishingRods.GrappleFallGuard;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin implements GrappleFallGuard {
    @Unique
    private boolean igny$grappleFallGuard;

    @Override
    public void igny$setGrappleFallGuard(boolean active) {
        this.igny$grappleFallGuard = active;
    }

    @Override
    public boolean igny$getGrappleFallGuard() {
        return this.igny$grappleFallGuard;
    }

    //#if >= 1.21.5
    //$$@Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    //$$private void igny$skipGrappleFallDamage(double fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
    //#else
    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void igny$skipGrappleFallDamage(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
    //#endif
        if (!IGNYSettings.GRAPPLING_FISHING_RODS.value() || !igny$getGrappleFallGuard()) {
            return;
        }

        igny$setGrappleFallGuard(false);
        cir.setReturnValue(false);
    }
}
