package com.liuyue.igny.mixins.rule.noWitherEffect;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if MC>=12103
//$$ import net.minecraft.server.level.ServerLevel;
//#endif

@Mixin(WitherSkeleton.class)
public class WitherSkeletonMixin {
    @Inject(method ="doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    private void doHurtTarget(
            //#if MC>=12103
            //$$ ServerLevel level,
            //#endif
            Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.noWitherEffect) {
            cir.setReturnValue(true);
        }
    }
}
