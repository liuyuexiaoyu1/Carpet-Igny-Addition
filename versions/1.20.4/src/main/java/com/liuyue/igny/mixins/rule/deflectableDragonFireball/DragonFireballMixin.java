package com.liuyue.igny.mixins.rule.deflectableDragonFireball;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DragonFireball.class)
public abstract class DragonFireballMixin extends AbstractHurtingProjectile {
    protected DragonFireballMixin(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "isPickable", at = @At("HEAD"), cancellable = true)
    private void igny$deflectableDragonFireball$pickable(CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.DEFLECTABLE_DRAGON_FIREBALL.value()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void igny$deflectableDragonFireball$hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!IGNYSettings.DEFLECTABLE_DRAGON_FIREBALL.value()) {
            return;
        }
        DragonFireball self = (DragonFireball) (Object) this;
        if (self.isInvulnerableTo(source)) {
            cir.setReturnValue(false);
            return;
        }
        this.markHurt();
        Entity attacker = source.getEntity();
        if (attacker == null) {
            cir.setReturnValue(false);
            return;
        }
        if (!self.level().isClientSide) {
            Vec3 look = attacker.getLookAngle();
            self.setDeltaMovement(look);
            this.xPower = look.x * 0.1;
            this.yPower = look.y * 0.1;
            this.zPower = look.z * 0.1;
            self.setOwner(attacker);
        }
        cir.setReturnValue(true);
    }
}
