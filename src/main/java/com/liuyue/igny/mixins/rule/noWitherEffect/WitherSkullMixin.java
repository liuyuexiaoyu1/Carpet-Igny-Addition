package com.liuyue.igny.mixins.rule.noWitherEffect;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkull.class)
public class WitherSkullMixin {
    @Inject(method="onHitEntity",at= @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    private void onHitEntity(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (IGNYSettings.noWitherEffect) {
            ci.cancel();
        }
    }
}
