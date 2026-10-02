package com.liuyue.igny.mixins.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.liuyue.igny.utils.TranslationOwnership;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Consumer;

@Mixin(CarpetServer.class)
public class CarpetServerMixin {

    @SuppressWarnings("unchecked")
    @WrapOperation(
            method = "onGameStarted",
            at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V")
    )
    private static void igny$trackCurrentExtension(List<CarpetExtension> instance, Consumer<?> consumer, Operation<Void> original) {
        Consumer<CarpetExtension> action = (Consumer<CarpetExtension>) consumer;
        for (CarpetExtension extension : instance) {
            TranslationOwnership.enter(extension);
            try {
                action.accept(extension);
            } finally {
                TranslationOwnership.exit();
            }
        }
    }
}
