package com.liuyue.igny.mixins.features.logger.piston;

import carpet.CarpetSettings;
import com.liuyue.igny.helper.PistonResolveContext;
import com.liuyue.igny.logging.IGNYLoggerRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PistonStructureResolver.class)
public abstract class PistonStructureResolverMixin {

    @Shadow
    @Final
    private List<BlockPos> toPush;
    @Unique
    private static final ThreadLocal<BlockPos> FIRST_FAILED = new ThreadLocal<>();

    @WrapOperation(
            method = {"resolve", "addBlockLine", "addBranchingBlocks"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;isPushable(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;ZLnet/minecraft/core/Direction;)Z")
    )
    private boolean wrapIsPushable(BlockState state, Level level, BlockPos pos, Direction dir1, boolean allowDestroy, Direction dir2, Operation<Boolean> original) {
        boolean result = original.call(state, level, pos, dir1, allowDestroy, dir2);
        if (!IGNYLoggerRegistry.__piston ) return result;
        if (PistonResolveContext.isRecording() && !result && FIRST_FAILED.get() == null) {
            FIRST_FAILED.set(pos);
        }
        return result;
    }

    @Inject(method = "resolve", at = @At("RETURN"))
    private void onReturn(CallbackInfoReturnable<Boolean> cir) {
        if (!IGNYLoggerRegistry.__piston ) return;
        if (PistonResolveContext.isRecording()) {
            if (!cir.getReturnValueZ() && PistonResolveContext.getFailureReason() == null) {
                BlockPos failed = FIRST_FAILED.get();
                if (failed != null) {
                    PistonResolveContext.setFailureReason(new PistonResolveContext.FailureReason(PistonResolveContext.FailureType.UNPUSHABLE_BLOCK, failed));
                }
            }
            FIRST_FAILED.remove();
        }
    }

    @Inject(
            method = "addBlockLine",
            at = @At(
                    value = "RETURN"
            )
    )
    private void wrapFirstSizeCheck(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (!IGNYLoggerRegistry.__piston ) return;
        if (PistonResolveContext.isRecording() && !cir.getReturnValueZ() && this.toPush.size() >= CarpetSettings.pushLimit) {
            PistonResolveContext.setFailureReason(
                    new PistonResolveContext.FailureReason(
                            PistonResolveContext.FailureType.TOO_MANY_BLOCKS
                    )
            );
        }
    }
}