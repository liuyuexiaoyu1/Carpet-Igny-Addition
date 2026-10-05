package com.liuyue.igny.mixins.rule.windChargeNoBlockStateChange;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow
    public Explosion.BlockInteraction getBlockInteraction() {
        throw new AssertionError();
    }

    @WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean explode(Entity entity, DamageSource source, float amount, Operation<Boolean> original) {
        if (IGNYSettings.WIND_CHARGE_NO_BLOCK_STATE_CHANGE.value()
                && entity instanceof net.minecraft.world.entity.decoration.HangingEntity
                && this.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK) {
            return false;
        }
        return original.call(entity, source, amount);
    }
}
