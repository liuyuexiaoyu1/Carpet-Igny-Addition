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

import java.util.List;

import carpet.utils.Translations;

@Mixin(SettingsManager.class)
public abstract class SettingsManagerMixin {
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
        if (!IGNYSettings.showRuleChangeHistory) return;
        if (rule != null) {
            List<RuleChangeDataManager.RuleChangeRecord> history = RuleChangeDataManager.getLastChange(rule.name());
            if (!history.isEmpty()) {
                for (RuleChangeDataManager.RuleChangeRecord lastChange : history) {
                    if (lastChange.isValid()) {
                        carpet.utils.Messenger.m(source,
                                "g  " + Translations.tr("igny.settings.record.operator", "Operator") + ": ", "w " + lastChange.sourceName,
                                "g  " + Translations.tr("igny.settings.record.change_time", "ChangeTime") + ": ", "w " + lastChange.formattedTime,
                                "g  " + Translations.tr("igny.settings.record.raw_value", "RawValue") + ": ", "w " + objectToString(lastChange.rawValue),
                                "g  " + Translations.tr("igny.settings.record.new_value", "NewValue") + ": ", "w " + lastChange.userInput
                        );
                    }
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

    @Inject(method = {"setRule", "setDefault"}, at= @At(value = "INVOKE", target = "Lcarpet/api/settings/CarpetRule;set(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void onSetRuleValue(CommandSourceStack source, CarpetRule<?> rule, String stringValue, CallbackInfoReturnable<Integer> cir){
        if (IGNYSettings.showRuleChangeHistory) {
            RuleChangeTracker.ruleChanged(source, rule, stringValue);
        }
    }

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
        if (settingsManager.equals(CarpetServer.settingsManager)) {
            Messenger.m(
                    source,
                    Messenger.c(
                            String.format("g %s ", IGNYServer.fancyName),
                            String.format("g %s: ", Translations.tr("igny.settings.command.version", "Version")),
                            String.format("g %s ", IGNYServerMod.getVersion()),
                            String.format("g (%s: %d)", Translations.tr("igny.settings.command.total_rules", "total rules"), IGNYServer.ruleCount)
                    )
            );
        }
    }

}