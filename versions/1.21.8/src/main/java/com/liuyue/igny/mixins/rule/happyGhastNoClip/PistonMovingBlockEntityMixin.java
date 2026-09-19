package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.helper.happyGhastNoClip.NoClipHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PistonMovingBlockEntity.class, priority = 1100)
public class PistonMovingBlockEntityMixin {
    @Inject(method = "moveEntityByPiston", at = @At("HEAD"), cancellable = true)
    private static void moveEntityByPiston(Direction direction, Entity instance, double d, Direction direction2, CallbackInfo ci) {
        if (NoClipHelper.isActiveGhastOrRider(instance)) {
            ci.cancel();
        }
    }

    @WrapOperation(method = "moveCollidedEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"))
    private static void setDeltaMovement(Entity instance, double d, double e, double f, Operation<Void> original) {
        if (NoClipHelper.isActiveGhastOrRider(instance)) {
            return;
        }

        original.call(instance, d, e, f);
    }
}
