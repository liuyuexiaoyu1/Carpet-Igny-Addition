package com.liuyue.igny.mixins.features.optimizations;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.optimizations.IEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "travel",at = @At(value = "HEAD"),cancellable = true)
    private void travel(Vec3 vec3, CallbackInfo ci){
        LivingEntity self = (LivingEntity) (Object) this;
        if (((IEntity)self).carpet_Igny_Addition$getCrammingCount() >= IGNYSettings.optimizedEntityLimit){
            ci.cancel();
        }
    }

    @Inject(method = "pushEntities",at = @At(value = "HEAD"), cancellable = true)
    private void pushEntities(CallbackInfo ci){
        LivingEntity self = (LivingEntity) (Object) this;
        if (((IEntity)self).carpet_Igny_Addition$getCrammingCount() >= IGNYSettings.optimizedEntityLimit){
            ci.cancel();
        }
    }
}
