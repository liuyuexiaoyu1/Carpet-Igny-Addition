package com.liuyue.igny.mixins.rule.liquidNeverSpread;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if >= 1.21.2
/*$$import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;$$*/
//#endif

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {
    @Inject(method = "spread", at = @At(value = "HEAD"), cancellable = true)
    private void spread(
            Level level, //#replace >= 1.21.2 ? ServerLevel level,
            BlockPos pos,
            //?>= 1.21.2 ? BlockState blockState,
            FluidState state, CallbackInfo ci) {
        if (!IGNYSettings.LIQUID_NEVER_SPREAD.value().equals("false")) {
            if (IGNYSettings.LIQUID_NEVER_SPREAD.value().equals("true") || state.isSource()) {
                ci.cancel();
            }
        }
    }
}
