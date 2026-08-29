package com.liuyue.igny.mixins.rule.containerBreakNoRandomMomentum;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Containers.class)
public class ContainersMixin {
    @WrapOperation(method = "dropItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"))
    private static void setDeltaMovement(ItemEntity instance, double x, double y, double z, Operation<Void> original) {
        if (IGNYSettings.CONTAINER_BREAK_NO_RANDOM_MOMENTUM.value()) {
            return;
        }
        original.call(instance, x, y, z);
    }
}
