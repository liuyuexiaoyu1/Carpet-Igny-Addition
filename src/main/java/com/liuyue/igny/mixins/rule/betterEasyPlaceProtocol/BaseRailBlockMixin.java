package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseRailBlock.class)
public abstract class BaseRailBlockMixin {

    @Inject(
            method = "updateState(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Z)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void igny_cancelUpdateState(BlockState state, Level world, BlockPos pos, boolean forceUpdate, CallbackInfoReturnable<BlockState> cir) {
        if (BetterEasyPlaceProtocolHandler.isEasyPlaceState()
                && BetterEasyPlaceProtocolHandler.hasPlaceFlag(BetterEasyPlaceProtocolHandler.EASY_PLACE_RAIL_BLOCK_NO_SHAPE_UPDATE)) {
            cir.setReturnValue(state);
        }
    }

    @Inject(
            method = "updateState(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void igny_cancelUpdateStateFromBlock(BlockState state, Level world, BlockPos pos, Block changedBlock, CallbackInfo ci) {
        if (BetterEasyPlaceProtocolHandler.isEasyPlaceState()
                && BetterEasyPlaceProtocolHandler.hasPlaceFlag(BetterEasyPlaceProtocolHandler.EASY_PLACE_RAIL_BLOCK_NO_SHAPE_UPDATE)) {
            ci.cancel();
        }
    }
}
