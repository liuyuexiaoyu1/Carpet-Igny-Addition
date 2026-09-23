package com.liuyue.igny.mixins.rule.liquidSourceCanDestroy;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClipContext.class)
public class ClipContextMixin {
    @ModifyVariable(method = "<init>(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;Lnet/minecraft/world/phys/shapes/CollisionContext;)V", at = @At(value = "HEAD"), argsOnly = true) //#replace <= 1.20.2 ? @ModifyVariable(method = "<init>", at = @At(value = "HEAD"), argsOnly = true)
    private static ClipContext.Fluid clip(ClipContext.Fluid value) {
        if (IGNYSettings.LIQUID_SOURCE_CAN_DESTROY.value() && value.equals(ClipContext.Fluid.NONE)) {
            return ClipContext.Fluid.SOURCE_ONLY;
        }
        return value;
    }
}
