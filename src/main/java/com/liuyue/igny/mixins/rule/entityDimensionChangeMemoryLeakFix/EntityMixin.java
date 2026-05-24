package com.liuyue.igny.mixins.rule.entityDimensionChangeMemoryLeakFix;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    @WrapOperation(method = "removeAfterChangingDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setRemoved(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void setRemoved(Entity instance, Entity.RemovalReason removalReason, Operation<Void> original) {
        if (IGNYSettings.ENTITY_DIMENSION_CHANGE_MEMORY_LEAK_FIX.value()) {
            instance.remove(removalReason);
        }
        original.call(instance, removalReason);
    }
}