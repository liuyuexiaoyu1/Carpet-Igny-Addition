package com.liuyue.igny.mixins.carpet;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import com.liuyue.igny.utils.TranslationOwnership;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsManager.class)
public class RuleOwnerTrackerMixin {
    @Inject(method = "addCarpetRule", at = @At(value = "HEAD"))
    private void igny$recordRuleOwner(CarpetRule<?> rule, CallbackInfo ci) {
        TranslationOwnership.recordRule(rule.name());
    }
}
