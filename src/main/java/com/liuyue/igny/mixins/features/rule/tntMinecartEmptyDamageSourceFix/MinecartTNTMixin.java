package com.liuyue.igny.mixins.features.rule.tntMinecartEmptyDamageSourceFix;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if MC >= 12103
//$$ import net.minecraft.server.level.ServerLevel;
//#endif

@Mixin(MinecartTNT.class)
public abstract class MinecartTNTMixin {

    @Shadow protected abstract Item getDropItem();

    @Unique
    private DamageSource ThreadLocal;

    @Inject(
            //#if MC >= 12103
            //$$ method = "hurtServer",
            //#else
            method = "hurt",
            //#endif
            at = @At("HEAD"), cancellable = true)
    private void onHurt(
            //#if MC >= 12103
            //$$ ServerLevel serverLevel,
            //#endif
            DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.tntMinecartEmptyDamageSourceFix) {
            var direct = source.getDirectEntity();
            if (direct instanceof AbstractArrow arrow && arrow.isOnFire()) {
                MinecartTNT minecartTNT = (MinecartTNT) (Object) this;
                this.ignitionSource = minecartTNT.damageSources().explosion(minecartTNT, source.getEntity());
                explodeWithCorrectSource(arrow.getDeltaMovement().lengthSqr(), cir);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    private void onDestroy(
            //#if MC >= 12103
            //$$ ServerLevel serverLevel,
            //#endif
            DamageSource source, CallbackInfo ci) {
        if (IGNYSettings.tntMinecartEmptyDamageSourceFix) {
            MinecartTNT self = (MinecartTNT) (Object) this;
            double speed = self.getDeltaMovement().horizontalDistanceSqr();
            if (!newDamageSourceIgnitesTnt(source) && speed < 0.01D) {
                self.destroy(
                        //#if MC >= 12103
                        //$$ serverLevel,
                        //#endif
                        //#if MC < 12103
                        //$$ source
                        //#else
                        this.getDropItem()
                        //#endif
                );
                ci.cancel();
                return;
            }
            if (self.getFuse() < 0) {
                this.ignitionSource = source;
                self.primeFuse();
            }
            ci.cancel();
        }
    }

    @Inject(method = "explode(D)V", at = @At("HEAD"), cancellable = true)
    private void explodeWithCorrectSource(double d, CallbackInfo ci) {
        if (IGNYSettings.tntMinecartEmptyDamageSourceFix) {
            MinecartTNT self = (MinecartTNT) (Object) this;
            if (!self.level().isClientSide()) {
                double e = Math.min(Math.sqrt(d), 5.0);
                self.level().explode(
                        self,
                        this.ignitionSource,
                        null,
                        self.getX(), self.getY(), self.getZ(),
                        (float) (4.0F + ((EntityInvoker) self).getRandom().nextDouble() * 1.5F * e),
                        false,
                        Level.ExplosionInteraction.TNT
                );
                self.discard();
            }
            ci.cancel();
        }
    }
    @Unique
    private static boolean newDamageSourceIgnitesTnt(DamageSource source) {
        return source.getDirectEntity() instanceof AbstractArrow arrow && arrow.isOnFire()
                || source.is(DamageTypeTags.IS_FIRE)
                || source.is(DamageTypeTags.IS_EXPLOSION);
    }

}