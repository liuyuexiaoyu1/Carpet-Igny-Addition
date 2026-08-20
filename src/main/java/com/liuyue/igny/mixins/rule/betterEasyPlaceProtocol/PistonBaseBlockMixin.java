package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PistonBaseBlock.class, priority = 900)
public abstract class PistonBaseBlockMixin {
    @WrapOperation(
            method = "onPlace",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;checkIfExtend(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V")
    )
    private static void igny_skipCheckIfExtend(PistonBaseBlock instance, Level level, BlockPos pos, BlockState state, Operation<Void> original) {
        if (!BetterEasyPlaceProtocolHandler.isEasyPlaceState() ||
                !BetterEasyPlaceProtocolHandler.hasPlaceFlag(BetterEasyPlaceProtocolHandler.EASY_PLACE_PISTON_NO_UPDATE)) {
            original.call(instance, level, pos, state);
        }
    }

    @WrapOperation(
            method = "setPlacedBy",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;checkIfExtend(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V")
    )
    private static void igny_skipCheckIfExtendOnPlacedBy(PistonBaseBlock instance, Level level, BlockPos pos, BlockState state, Operation<Void> original) {
        if (!BetterEasyPlaceProtocolHandler.isEasyPlaceState() ||
                !BetterEasyPlaceProtocolHandler.hasPlaceFlag(BetterEasyPlaceProtocolHandler.EASY_PLACE_PISTON_NO_UPDATE)) {
            original.call(instance, level, pos, state);
        }
    }

    //#if MC >= 12103
    //$$ @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    //$$ private void igny_cancelNeighborUpdate(BlockState state, Level world, BlockPos pos, Block block, net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving, CallbackInfo ci) {
    //#else
    @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    private void igny_cancelNeighborUpdate(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving, CallbackInfo ci) {
    //#endif
        if (!BetterEasyPlaceProtocolHandler.isEasyPlaceState()) {
            return;
        }
        if (pos.equals(BetterEasyPlaceProtocolHandler.getPlaceTargetPos()) && block == BetterEasyPlaceProtocolHandler.getPlaceTargetBlock()) {
            ci.cancel();
        }
        Direction facing = state.getValue(PistonBaseBlock.FACING);
        if (pos.relative(facing).equals(IGNYSettings.lastPistonHeadPos) && world.getBlockState(IGNYSettings.lastPistonHeadPos).is(Blocks.PISTON_HEAD)) {
            IGNYSettings.lastPistonHeadPos = null;
            ci.cancel();
        }
    }
}