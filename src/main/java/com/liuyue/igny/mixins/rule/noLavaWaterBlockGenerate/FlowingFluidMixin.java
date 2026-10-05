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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {
    //#if >= 26.3
    /*$$@WrapOperation(method = "spreadTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean igny$setBlockAndUpdate(LevelAccessor instance, BlockPos pos, BlockState state, Operation<Boolean> original)$$*/
    //#else
    @WrapOperation(method = "spreadTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean igny$setBlock(LevelAccessor instance, BlockPos pos, BlockState state, int flags, Operation<Boolean> original)
    //#endif
    {
        if (igny$wouldMixLavaAndWater(instance, pos, state)) {
            return false;
        }
        return original.call(instance, pos, state, flags); //#replace >= 26.3 ? return original.call(instance, pos, state);
    }

    @Unique
    private static boolean igny$wouldMixLavaAndWater(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!IGNYSettings.NO_LAVA_WATER_BLOCK_GENERATE.value()) {
            return false;
        }
        FluidState currentFluid = level.getBlockState(pos).getFluidState();
        FluidState newFluid = state.getFluidState();
        return (currentFluid.is(FluidTags.LAVA) && newFluid.is(FluidTags.WATER))
                || (currentFluid.is(FluidTags.WATER) && newFluid.is(FluidTags.LAVA));
    }
}
