package com.liuyue.igny.mixins.rule.mobPathNavigationRealTimeTimerReintroduced;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.Util;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PathNavigation.class)
public class PathNavigationMixin {
    @WrapOperation(method = "doStuckDetection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getGameTime()J"))
    private long doStuckDetection(Level instance, Operation<Long> original) {
        return IGNYSettings.mobPathNavigationRealTimeTimerReintroduced ? Util.getMillis() : original.call(instance);
    }

    @ModifyConstant(method = "doStuckDetection", constant = @Constant(doubleValue = 20.0))
    private double doStuckDetection(double original) {
        return IGNYSettings.mobPathNavigationRealTimeTimerReintroduced ? Util.getMillis() : original;
    }
}
