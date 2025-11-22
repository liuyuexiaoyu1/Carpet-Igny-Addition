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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;
import carpet.utils.Translations;

@Mixin(SettingsManager.class)
public class SettingsManagerMixin {

    private static final ThreadLocal<carpet.api.settings.CarpetRule<?>> CURRENT_RULE = new ThreadLocal<>();

    @Inject(method = "displayRuleMenu", at = @At("HEAD"))
    private void captureCurrentRule(CommandSourceStack source, CarpetRule<?> rule, CallbackInfoReturnable<Integer> cir) {
        CURRENT_RULE.set(rule);
    }

    @Inject(method = "displayRuleMenu", at = @At("RETURN"))
    private void clearCurrentRule(CommandSourceStack source, CarpetRule<?> rule, CallbackInfoReturnable<Integer> cir) {
        CURRENT_RULE.remove();
    }

    @Inject(
            method = "displayRuleMenu",
            at = @At(
                    value = "INVOKE",
                    target = "Lcarpet/utils/Messenger;m(Lnet/minecraft/commands/CommandSourceStack;[Ljava/lang/Object;)V",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void addOperationInfoAfterCurrentValue(
            CommandSourceStack source, CarpetRule<?> rule, CallbackInfoReturnable<Integer> cir, String displayName, List tags) {
        // 检查是否启用显示功能
        if (!IGNYSettings.ShowRuleChangeHistory) {
            return;
        }

        carpet.api.settings.CarpetRule<?> currentRule = CURRENT_RULE.get();
        if (currentRule != null) {
            Optional<RuleChangeDataManager.RuleChangeRecord> lastChange =
                    RuleChangeDataManager.getLastChange(currentRule.name());

            // 检查记录是否存在、有效且不是默认值
            if (lastChange.isPresent()) {
                RuleChangeDataManager.RuleChangeRecord record = lastChange.get();

                if (record.isValid()) {
                    carpet.utils.Messenger.m(source, new Object[]{
                            "g  操作源: ", "w " + record.sourceName,
                            "g , 变更时间: ", "w " + record.formattedTime,
                            "g , 原始值: ", "w " + objectToString(record.defaultValue)
                    });
                }
            }
        }
    }


    private String objectToString(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof Boolean) return (Boolean) obj ? "true" : "false";
        return obj.toString();
    }
    @Inject(method = "setRule",at= @At(value = "INVOKE", target = "Lcarpet/api/settings/CarpetRule;set(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private void onRuleChanged(CommandSourceStack source, CarpetRule<?> rule, String newValue, CallbackInfoReturnable<Integer> cir){
        RuleChangeTracker.ruleChanged(source, rule, newValue);
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
        if ((Object)this == CarpetServer.settingsManager) {
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