package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DiodeBlock.class, priority = 900)
public abstract class DiodeBlockMixin {
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
    }
}