package com.liuyue.igny.mixins.rule.doubleAirDragReintroduced;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.doubleAirDrag.AirDragHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;", ordinal = 0))
    private Vec3 igny$doubleAirDrag(Vec3 instance, double factorX, double factorY, double factorZ, Operation<Vec3> original) {
        if (!IGNYSettings.DOUBLE_AIR_DRAG_REINTRODUCED.value()) {
            return original.call(instance, factorX, factorY, factorZ);
        }
        return original.call(instance, factorX, AirDragHelper.restoreDouble(factorY), factorZ);
    }
}
