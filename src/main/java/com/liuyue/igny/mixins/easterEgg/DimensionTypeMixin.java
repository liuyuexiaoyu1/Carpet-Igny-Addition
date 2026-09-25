package com.liuyue.igny.mixins.easterEgg;

import com.liuyue.igny.utils.FestivalUtil;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {
    @Inject(method = "moonPhase", at = @At(value = "HEAD"), cancellable = true)
    private void moonPhase(long dayTime, CallbackInfoReturnable<Integer> cir) {
        if (FestivalUtil.isMidAutumnDay()) {
            cir.setReturnValue(0);
        }
    }
}
