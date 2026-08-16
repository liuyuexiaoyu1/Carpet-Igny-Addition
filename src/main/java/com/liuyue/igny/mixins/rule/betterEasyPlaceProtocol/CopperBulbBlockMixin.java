package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

//#if MC >= 12003

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CopperBulbBlock.class, priority = 900)
public abstract class CopperBulbBlockMixin {
    @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    private void igny_cancelOnPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
        if (!BetterEasyPlaceProtocolHandler.isEasyPlaceState()) {
            return;
        }
        ci.cancel();
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
        ci.cancel();
    }
}
//#endif