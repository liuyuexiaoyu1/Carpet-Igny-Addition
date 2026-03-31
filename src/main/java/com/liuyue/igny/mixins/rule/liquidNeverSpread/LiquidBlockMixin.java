package com.liuyue.igny.mixins.rule.liquidNeverSpread;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {
    @Inject(method = "shouldSpreadLiquid", at = @At(value = "HEAD"), cancellable = true)
    private void shouldSpreadLiquid(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!IGNYSettings.liquidNeverSpread.equals("false")) {
            if (IGNYSettings.liquidNeverSpread.equals("true") || state.getFluidState().isSource()) {
                cir.setReturnValue(false);
            }
        }
    }
}
