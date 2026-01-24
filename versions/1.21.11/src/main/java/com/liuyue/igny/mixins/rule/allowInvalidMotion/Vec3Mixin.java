package com.liuyue.igny.mixins.rule.allowInvalidMotion;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Vec3.class)
public class Vec3Mixin {
    @Inject(method = "isFinite", at = @At(value = "HEAD"), cancellable = true)
    private void isFinite(CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.allowInvalidMotion) cir.setReturnValue(true);
    }
}
