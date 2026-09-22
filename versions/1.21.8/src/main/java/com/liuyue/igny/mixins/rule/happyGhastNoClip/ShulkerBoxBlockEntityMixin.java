package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.helper.happyGhastNoClip.NoClipHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin {
    @WrapOperation(method = "moveCollidedEntities", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getPistonPushReaction()Lnet/minecraft/world/level/material/PushReaction;"
    ))
    private PushReaction getPistonPushReaction(Entity instance, Operation<PushReaction> original) {
        if (NoClipHelper.isActiveGhastOrRider(instance)) {
            return PushReaction.IGNORE; //#replace >= 26.3 ? return PushReaction.IGNORE_ENTITY;
        }

        return original.call(instance);
    }
}
