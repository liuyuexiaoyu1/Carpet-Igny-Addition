package com.liuyue.igny.mixins.features.optimizations;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.optimizations.IEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(SetEntityLookTarget.class)
public class SetEntityLookTargetMixin {
    @Inject(method = "method_47063", at = @At(value = "HEAD"), cancellable = true)
    private static void create(BehaviorBuilder.Instance<?> instance, MemoryAccessor<?, ?> memoryAccessor, Predicate<?> predicate, float f, MemoryAccessor<?, ?>  memoryAccessor2, ServerLevel serverLevel, LivingEntity livingEntity, long l, CallbackInfoReturnable<Boolean> cir){
        if (livingEntity instanceof Piglin && ((IEntity)livingEntity).carpet_Igny_Addition$getCrammingCount() >= IGNYSettings.optimizedEntityLimit){
            cir.setReturnValue(false);
        }
    }
}
