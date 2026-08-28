package com.liuyue.igny.mixins.rule.instantSpreadLiquid;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
//#if MC >= 12103
//$$ import net.minecraft.server.level.ServerLevel;
//#else
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
//#endif
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {
    //#if MC >= 12103
    //$$ @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V"))
    //$$ private void scheduleTick1(ServerLevel instance, BlockPos pos, Fluid fluid, int i, Operation<Void> original)
    //#else
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V"))
    private void scheduleTick(Level instance, BlockPos pos, Fluid fluid, int i, Operation<Void> original)
    //#endif
    {
        if (IGNYSettings.INSTANT_SPREAD_LIQUID.value() && instance instanceof ServerLevel serverLevel) {
                ((ServerLevelInvoker) serverLevel).invokeTickFluid(pos, fluid);
            return;
        }
        original.call(instance, pos, fluid, i);
    }
}
