package com.liuyue.igny.mixins.easterEgg;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.FestivalUtil;
import net.minecraft.world.level.MoonPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MoonPhase.class)
public class MoonPhaseMixin {
    @Inject(method = "index", at = @At(value = "HEAD"), cancellable = true)
    private void index(CallbackInfoReturnable<Integer> cir) {
        if (IGNYSettings.FESTIVE_EASTER_EGG.value() && FestivalUtil.isMidAutumnDay()) {
            cir.setReturnValue(MoonPhase.FULL_MOON.ordinal());
        }
    }
}
