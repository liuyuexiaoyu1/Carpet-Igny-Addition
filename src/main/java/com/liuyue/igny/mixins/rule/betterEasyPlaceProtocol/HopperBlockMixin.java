package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopperBlock.class)
public class HopperBlockMixin {
    @Inject(method = "checkPoweredState", at = @At(value = "HEAD"), cancellable = true)
    private void checkPoweredState(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (!BetterEasyPlaceProtocolHandler.isEasyPlaceState()) {
            return;
        }
        if (pos.equals(BetterEasyPlaceProtocolHandler.getPlaceTargetPos()) && state.getBlock() == BetterEasyPlaceProtocolHandler.getPlaceTargetBlock()) {
            ci.cancel();
        }
    }
}
