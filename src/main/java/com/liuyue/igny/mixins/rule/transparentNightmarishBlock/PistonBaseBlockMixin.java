package com.liuyue.igny.mixins.rule.transparentNightmarishBlock;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.RuleUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {
    @WrapMethod(method = "moveBlocks")
    private boolean moveBlocks(Level level, BlockPos pos, Direction facing, boolean extending, Operation<Boolean> original) {
        try {
            if (IGNYSettings.transparentNightmarishBlock) {
                IGNYSettings.movingBlocks.set(true);
            }
            return original.call(level, pos, facing, extending);
        } finally {
            IGNYSettings.movingBlocks.set(false);
        }
    }

    @WrapOperation(method = "triggerEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean triggerEvent(Level instance, BlockPos pos, boolean isMoving, Operation<Boolean> original) {
        if (IGNYSettings.transparentNightmarishBlock) {
            if (RuleUtil.isNightmarishBlock(instance.getBlockState(pos).getBlock())) {
                return false;
            }
            return original.call(instance, pos, isMoving);
        }
        return original.call(instance, pos, isMoving);
    }

    @Inject(method = "isPushable", at = @At(value = "RETURN"), cancellable = true)
    private static void isPushable(BlockState state, Level level, BlockPos pos, Direction movementDirection, boolean allowDestroy, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.transparentNightmarishBlock) {
            if (RuleUtil.isNightmarishBlock(state.getBlock())) {
                cir.setReturnValue(allowDestroy);
            }
        }
    }
}
