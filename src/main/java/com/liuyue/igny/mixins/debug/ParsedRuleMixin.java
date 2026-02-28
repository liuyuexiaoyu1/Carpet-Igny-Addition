package com.liuyue.igny.mixins.debug;

import carpet.utils.Translations;
import com.liuyue.igny.IGNYServer;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Translations.class)
public class ParsedRuleMixin {
    @Inject(method = "trOrNull",at = @At("RETURN"), cancellable = true)
    private static void trOrNull(String key, CallbackInfoReturnable<String> cir) {
        if (cir.getReturnValue() == null && FabricLoader.getInstance().isDevelopmentEnvironment()) {
            cir.setReturnValue("IGNY debug");
            IGNYServer.LOGGER.warn("{} is null", key);
        }
    }
}