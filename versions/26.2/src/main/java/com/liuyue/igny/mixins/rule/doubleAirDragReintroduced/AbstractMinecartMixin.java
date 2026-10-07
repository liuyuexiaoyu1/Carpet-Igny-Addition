package com.liuyue.igny.mixins.rule.doubleAirDragReintroduced;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.doubleAirDrag.AirDragHelper;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {
    @WrapOperation(method = "comeOffTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;", ordinal = 1))
    private Vec3 igny$doubleAirDrag(Vec3 instance, double scale, Operation<Vec3> original) {
        if (!IGNYSettings.DOUBLE_AIR_DRAG_REINTRODUCED.value()) {
            return original.call(instance, scale);
        }
        return original.call(instance, AirDragHelper.restoreDouble(scale));
    }
}
