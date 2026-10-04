package com.liuyue.igny.mixins.rule.noLavaWaterBlockGenerate;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {
    @WrapOperation(method = "spreadTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean setBlock(LevelAccessor instance, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
        if (!IGNYSettings.NO_LAVA_WATER_BLOCK_GENERATE.value()) {
            return original.call(instance, pos, state, i);
        }
        BlockState current = instance.getBlockState(pos);
        FluidState currentFluid = current.getFluidState();
        FluidState newFluid = state.getFluidState();
        if ((currentFluid.is(FluidTags.LAVA) && newFluid.is(FluidTags.WATER))
                || (currentFluid.is(FluidTags.WATER) && newFluid.is(FluidTags.LAVA))) {
            return false;
        }
        return original.call(instance, pos, state, i);
    }
}
