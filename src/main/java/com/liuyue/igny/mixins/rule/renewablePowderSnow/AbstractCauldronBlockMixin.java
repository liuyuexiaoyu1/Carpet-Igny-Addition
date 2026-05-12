package com.liuyue.igny.mixins.rule.renewablePowderSnow;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronBlockMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/AbstractCauldronBlock;receiveStalactiteDrip(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;)V"))
    private void receiveStalactiteDrip(AbstractCauldronBlock instance, BlockState state, Level level, BlockPos pos, Fluid fluid, Operation<Void> original) {
        BlockState blockState = getSourceBlockAtTop(level, pos);
        if (IGNYSettings.renewablePowderSnow && blockState.is(Blocks.POWDER_SNOW)) {
            original.call(instance, blockState, level, pos, fluid);
            return;
        }
        original.call(instance, state, level, pos, fluid);
    }

    @Unique
    private static BlockState getSourceBlockAtTop(Level level, BlockPos cauldronPos) {
        BlockPos tipPos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(level, cauldronPos);
        if (tipPos != null) {
            BlockState tipState = level.getBlockState(tipPos);

            Optional<BlockPos> rootPos = PointedDripstoneBlockAccessor.findRootBlock(level, tipPos, tipState, 11);

            if (rootPos.isPresent()) {
                BlockPos sourcePos = rootPos.get().above();
                return level.getBlockState(sourcePos);
            }
        }
        return Blocks.AIR.defaultBlockState();
    }
}
