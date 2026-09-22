package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

//#if >= 1.20.3
import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
//$$ import com.liuyue.igny.utils.compat.DummyClass;
//#endif

import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = CopperBulbBlock.class, priority = 900) //#replace < 1.20.3 ? @Mixin(DummyClass.class)
public abstract class CopperBulbBlockMixin {
    //#if >= 1.20.3
    @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    private void igny_cancelOnPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
        if (!BetterEasyPlaceProtocolHandler.isEasyPlaceState()) {
            return;
        }
        ci.cancel();
    }

    //#if >= 1.21.3
    /*$$@Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    private void igny_cancelNeighborUpdate(BlockState state, Level world, BlockPos pos, Block block, net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving, CallbackInfo ci) {$$*/
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
    //#endif
}