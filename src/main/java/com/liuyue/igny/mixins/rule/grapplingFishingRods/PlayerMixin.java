package com.liuyue.igny.mixins.rule.grapplingFishingRods;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.grapplingFishingRods.GrappleFallGuard;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements GrappleFallGuard {

    @Unique
    private boolean igny$grappleImpulseActive;

    @Unique
    private Vec3 igny$grappleImpactPos;

    @Unique
    private int igny$grappleGraceTime;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void igny$startGrappleImpulse(Vec3 impactPos) {
        this.igny$grappleImpulseActive = true;
        this.igny$grappleImpactPos = impactPos;
        this.igny$grappleGraceTime = 40;
    }

    @Override
    public boolean igny$isGrappleImpulseActive() {
        return this.igny$grappleImpulseActive;
    }

    @Override
    public Vec3 igny$getGrappleImpactPos() {
        return this.igny$grappleImpactPos;
    }

    @Override
    public void igny$tickGrappleImpulse() {
        if (!this.igny$grappleImpulseActive) {
            return;
        }
        if (!IGNYSettings.GRAPPLING_FISHING_RODS.value()) {
            this.igny$grappleImpulseActive = false;
            this.igny$grappleImpactPos = null;
            this.igny$grappleGraceTime = 0;
            return;
        }
        if (this.igny$grappleGraceTime > 0) {
            this.igny$grappleGraceTime--;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void igny$onPlayerTick(CallbackInfo ci) {
        igny$tickGrappleImpulse();
    }

    //#if >= 1.21.5
    /*$$@Inject(method = "causeFallDamage", at = @At(value = "HEAD"), cancellable = true)
        private void igny$skipGrappleFallDamage(double fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {$$*/
    //#else
    @Inject(method = "causeFallDamage", at = @At(value = "HEAD"), cancellable = true)
    private void igny$skipGrappleFallDamage(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        //#endif
        if (!IGNYSettings.GRAPPLING_FISHING_RODS.value() || !igny$grappleImpulseActive) {
            return;
        }

        Player self = (Player) (Object) this;
        if (igny$grappleImpactPos == null) {
            return;
        }

        double startY = igny$grappleImpactPos.y;
        if (startY < self.getY()) {
            igny$grappleImpulseActive = false;
            igny$grappleImpactPos = null;
            igny$grappleGraceTime = 0;
            cir.setReturnValue(false);
            return;
        }
        float reduced = Math.min((float) fallDistance, (float)(startY - self.getY()));
        igny$grappleImpulseActive = false;
        igny$grappleImpactPos = null;
        igny$grappleGraceTime = 0;

        cir.setReturnValue(super.causeFallDamage(reduced, multiplier, source));
    }
}