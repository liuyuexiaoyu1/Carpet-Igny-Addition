package com.liuyue.igny.mixins.rule.instantSpreadLiquid;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
//#if MC >= 12103
//$$ import net.minecraft.world.level.ScheduledTickAccess;
//#else
import net.minecraft.world.level.LevelAccessor;
//#endif
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {
    @WrapOperation(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V"))
    private void scheduleTick(Level instance, BlockPos pos, Fluid fluid, int i, Operation<Void> original) {
        if (IGNYSettings.INSTANT_SPREAD_LIQUID.value() && instance instanceof ServerLevel serverLevel) {
            ((ServerLevelInvoker) serverLevel).invokeTickFluid(pos, fluid);
            return;
        }
        original.call(instance, pos, fluid, i);
    }

    //#if MC >= 12103
    //$$ @WrapOperation(method = "updateShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ScheduledTickAccess;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V"))
    //$$ private void scheduleTick2(ScheduledTickAccess instance, BlockPos pos, Fluid fluid, int delay, Operation<Void> original)
    //#else
    @WrapOperation(method = "updateShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V"))
    private void scheduleTick2(LevelAccessor instance, BlockPos pos, Fluid fluid, int delay, Operation<Void> original)
    //#endif
    {
        if (IGNYSettings.INSTANT_SPREAD_LIQUID.value() && instance instanceof ServerLevel serverLevel) {
            ((ServerLevelInvoker) serverLevel).invokeTickFluid(pos, fluid);
            return;
        }
        original.call(instance, pos, fluid, delay);
    }

    @WrapOperation(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V"))
    private void scheduleTick3(Level instance, BlockPos pos, Fluid fluid, int i, Operation<Void> original) {
        if (IGNYSettings.INSTANT_SPREAD_LIQUID.value() && instance instanceof ServerLevel serverLevel) {
            ((ServerLevelInvoker) serverLevel).invokeTickFluid(pos, fluid);
            return;
        }
        original.call(instance, pos, fluid, i);
    }
}
