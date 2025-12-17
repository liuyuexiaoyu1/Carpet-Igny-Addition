package com.liuyue.igny.mixins.rule.safeSoundSuppression;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/world/level/block/entity/CalibratedSculkSensorBlockEntity$VibrationUser")
public class CalibratedSculkSensorVibrationUserMixin {
    @Inject(method = "getBackSignal",at = @At("RETURN"), cancellable = true)
    private void getBackSignal(Level level, BlockPos blockPos, BlockState blockState, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == 0 && IGNYSettings.safeSoundSuppression) {
            cir.setReturnValue(16);
        }
    }
}
