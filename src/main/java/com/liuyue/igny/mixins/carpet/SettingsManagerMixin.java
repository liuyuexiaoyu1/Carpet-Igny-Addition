package com.liuyue.igny.mixins.carpet;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import carpet.utils.Messenger;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.data.RuleChangeDataManager;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.tracker.RuleChangeTracker;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import carpet.utils.Translations;

@Mixin(SettingsManager.class)
public abstract class SettingsManagerMixin {

    @Unique
    private static final ThreadLocal<carpet.api.settings.CarpetRule<?>> CURRENT_RULE = new ThreadLocal<>();

    @Inject(method = "displayRuleMenu", at = @At("HEAD"))
    private void captureCurrentRule(CommandSourceStack source, CarpetRule<?> rule, CallbackInfoReturnable<Integer> cir) {
        CURRENT_RULE.set(rule);
    }

    @Inject(method = "displayRuleMenu", at = @At("RETURN"))
    private void clearCurrentRule(CommandSourceStack source, CarpetRule<?> rule, CallbackInfoReturnable<Integer> cir) {
        CURRENT_RULE.remove();
    }
    @Unique
    private static final String RECORD_OPERATOR = "igny.settings.record.operator";

    @Unique
    private static final String CHANGE_TIME = "igny.settings.record.change_time";

    @Unique
    private static final String RAW_VALUE = "igny.settings.record.raw_value";

    @Inject(
            method = "displayRuleMenu",
            at = @At(
                    value = "INVOKE",
                    target = "Lcarpet/utils/Messenger;m(Lnet/minecraft/commands/CommandSourceStack;[Ljava/lang/Object;)V",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            )
    )
    private void addOperationInfoAfterCurrentValue(CommandSourceStack source, CarpetRule<?> rule, CallbackInfoReturnable<Integer> cir) {
        if (!IGNYSettings.showRuleChangeHistory) {
            return;
        }

        carpet.api.settings.CarpetRule<?> currentRule = CURRENT_RULE.get();
        if (currentRule != null) {
            Optional<RuleChangeDataManager.RuleChangeRecord> lastChange =
                    RuleChangeDataManager.getLastChange(currentRule.name());

            if (lastChange.isPresent()) {
                RuleChangeDataManager.RuleChangeRecord record = lastChange.get();

                if (record.isValid()) {
                    carpet.utils.Messenger.m(source,
                            "g  "+Translations.tr(RECORD_OPERATOR,"Operator")+": ", "w " + record.sourceName,
                            "g  "+Translations.tr(CHANGE_TIME,"ChangeTime")+": ", "w " + record.formattedTime,
                            "g  "+Translations.tr(RAW_VALUE,"RawValue")+": ", "w " + objectToString(record.rawValue)
                    );
                }
            }
        }
    }


    @Unique
    private String objectToString(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof Boolean) return (Boolean) obj ? "true" : "false";
        return obj.toString();
    }

    @Inject(method = "setRule",at= @At(value = "INVOKE", target = "Lcarpet/api/settings/CarpetRule;set(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private void onSetRuleValue(CommandSourceStack source, CarpetRule<?> rule, String stringValue, CallbackInfoReturnable<Integer> cir){
        if(IGNYSettings.showRuleChangeHistory) {
            RuleChangeTracker.ruleChanged(source, rule, stringValue);
        }
    }

    @Inject(method="setDefault",at= @At(value = "INVOKE", target = "Lcarpet/api/settings/CarpetRule;set(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private void onSetRuleDefaultValue(CommandSourceStack source, CarpetRule<?> rule, String stringValue, CallbackInfoReturnable<Integer> cir){
        if(IGNYSettings.showRuleChangeHistory) {
            RuleChangeTracker.ruleChanged(source, rule, stringValue);
        }
    }

    @Unique
    private static final String VERSION_TRANSLATION_KEY = "igny.settings.command.version";

    @Unique
    private static final String TOTAL_RULES_TRANSLATION_KEY = "igny.settings.command.total_rules";

    @Inject(
            method = "listAllSettings",
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "stringValue=carpet.settings.command.version",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lcarpet/api/settings/SettingsManager;getCategories()Ljava/lang/Iterable;",
                    ordinal = 0
            ),
            remap = false
    )
    private void printVersion(CommandSourceStack source, CallbackInfoReturnable<Integer> cir) {
        SettingsManager settingsManager = (SettingsManager) (Object) this;
        if (settingsManager == CarpetServer.settingsManager) {
            Messenger.m(
                    source,
                    Messenger.c(
                            String.format("g %s ", IGNYServer.fancyName),
                            String.format("g %s: ", Translations.tr(VERSION_TRANSLATION_KEY, "Version")),
                            String.format("g %s ", IGNYServerMod.getVersion()),
                            String.format("g (%s: %d)", Translations.tr(TOTAL_RULES_TRANSLATION_KEY, "total rules"), IGNYServer.ruleCount)
                    )
            );
        }
    }

}