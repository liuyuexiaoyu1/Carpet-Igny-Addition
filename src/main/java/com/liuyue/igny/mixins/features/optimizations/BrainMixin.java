package com.liuyue.igny.mixins.features.optimizations;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.optimizations.IEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.OneShot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Brain.class,priority = 1500)
public class BrainMixin {
    @Inject(method = "tickSensors",at = @At(value = "HEAD"),cancellable = true)
    private void tickSensors(ServerLevel serverLevel, LivingEntity livingEntity, CallbackInfo ci){
        if (((IEntity)livingEntity).carpet_Igny_Addition$getCrammingCount() >= IGNYSettings.optimizedEntityLimit){
            ci.cancel();
        }
    }

    @WrapOperation(method = "startEachNonRunningBehavior", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/BehaviorControl;tryStart(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;J)Z"))
    private boolean startEachNonRunningBehavior(BehaviorControl<?> instance, ServerLevel level, LivingEntity entity, long l, Operation<Boolean> original){
        if (!(instance instanceof OneShot<?>) && ((IEntity)entity).carpet_Igny_Addition$getCrammingCount() >= IGNYSettings.optimizedEntityLimit){
            return false;
        }
        return original.call(instance, level, entity, l);
    }
}
