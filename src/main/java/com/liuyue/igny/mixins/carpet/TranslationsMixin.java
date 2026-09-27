package com.liuyue.igny.mixins.carpet;

import carpet.CarpetExtension;
import carpet.utils.Translations;
import com.liuyue.igny.utils.TranslationOwnership;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Translations.class)
public class TranslationsMixin {
    @WrapOperation(
            method = "updateLanguage",
            at = @At(
                    value = "INVOKE",
                    target = "Lcarpet/CarpetExtension;canHasTranslations(Ljava/lang/String;)Ljava/util/Map;"
            )
    )
    private static Map<String, String> igny$captureExtensionLang(CarpetExtension instance, String lang, Operation<Map<String, String>> original) {
        Map<String, String> mapping = original.call(instance, lang);
        TranslationOwnership.recordLang(instance, mapping);
        return mapping;
    }

    @Inject(method = "tr(Ljava/lang/String;)Ljava/lang/String;", at = @At(value = "HEAD"), cancellable = true)
    private static void igny$tr(String key, CallbackInfoReturnable<String> cir) {
        Map<String, String> lang = igny$ownerLang(key);
        if (lang != null) {
            cir.setReturnValue(lang.getOrDefault(key, key));
        }
    }

    @Inject(method = "tr(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At(value = "HEAD"), cancellable = true)
    private static void igny$trWithFallback(String key, String fallback, CallbackInfoReturnable<String> cir) {
        Map<String, String> lang = igny$ownerLang(key);
        if (lang != null) {
            cir.setReturnValue(lang.getOrDefault(key, fallback));
        }
    }

    @Inject(method = "trOrNull(Ljava/lang/String;)Ljava/lang/String;", at = @At(value = "HEAD"), cancellable = true)
    private static void igny$trOrNull(String key, CallbackInfoReturnable<String> cir) {
        Map<String, String> lang = igny$ownerLang(key);
        if (lang != null && lang.containsKey(key)) {
            cir.setReturnValue(lang.get(key));
        }
    }

    @Inject(method = "hasTranslation(Ljava/lang/String;)Z", at = @At(value = "HEAD"), cancellable = true)
    private static void igny$hasTranslation(String key, CallbackInfoReturnable<Boolean> cir) {
        Map<String, String> lang = igny$ownerLang(key);
        if (lang != null) {
            cir.setReturnValue(lang.containsKey(key));
        }
    }

    @Unique
    private static Map<String, String> igny$ownerLang(String key) {
        int rule = key.indexOf(".rule.");
        if (rule < 0) {
            return null;
        }
        int nameEnd = key.indexOf('.', rule + 6);
        if (nameEnd < 0) {
            return null;
        }
        return TranslationOwnership.langOfRule(key.substring(rule + 6, nameEnd));
    }
}
