package com.liuyue.igny.mixins.rule.allowInvalidMotion;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    @WrapOperation(method = "push(DDD)V", at = @At(value = "INVOKE", target = "Ljava/lang/Double;isFinite(D)Z"))
    private static boolean allowInvalidMotion$wrapIsFinite(double d, Operation<Boolean> original) {
        if (IGNYSettings.allowInvalidMotion) return true;
        return original.call(d);
    }
}
