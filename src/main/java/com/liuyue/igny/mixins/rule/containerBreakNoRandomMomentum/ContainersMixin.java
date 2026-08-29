package com.liuyue.igny.mixins.rule.containerBreakNoRandomMomentum;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Containers.class)
public class ContainersMixin {
    @Unique private static boolean shouldRemove;

    @WrapOperation(method = "dropItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"))
    private static void setDeltaMovement(ItemEntity instance, double x, double y, double z, Operation<Void> original) {
        if (shouldRemove) {
            return;
        }
        original.call(instance, x, y, z);
    }

    @WrapMethod(method = "dropContents(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/Container;)V")
    private static void dropContents(Level level, BlockPos pos, Container inventory, Operation<Void> original) {
        if (IGNYSettings.CONTAINER_BREAK_NO_RANDOM_MOMENTUM.value()) shouldRemove = true;
        try {
            original.call(level, pos, inventory);
        } finally {
            shouldRemove = false;
        }
    }
}
