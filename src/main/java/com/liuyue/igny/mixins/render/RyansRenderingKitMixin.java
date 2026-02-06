package com.liuyue.igny.mixins.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "ml.mypals.ryansrenderingkit.RyansRenderingKit", remap = false)
@Pseudo
public class RyansRenderingKitMixin {
    @Inject(method = "onInitialize", at = @At("HEAD"), cancellable = true, require = 0)
    private void onInitialize(CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            ci.cancel();
        }
    }
}
