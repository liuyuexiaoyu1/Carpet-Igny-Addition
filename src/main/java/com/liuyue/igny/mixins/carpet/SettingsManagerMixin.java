package com.liuyue.igny.mixins.carpet;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import carpet.utils.Messenger;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.manager.RuleChangeDataManager;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.rule.RuleObserver;
import com.liuyue.igny.tracker.RuleChangeTracker;
import com.liuyue.igny.utils.ClassUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        if (rule != null) {
            if (IGNYSettings.showRuleSource) {
                String id = getModIdByRuleName(rule.name());
                String name = FabricLoader.getInstance()
                        .getModContainer(id)
                        .map(ModContainer::getMetadata)
                        .map(ModMetadata::getName)
                        .orElse("Unknown");
                Messenger.m(source,
                        "g " + Translations.tr("igny.settings.source", "Source") + ": ", "w " + name
                );
            }
            if (IGNYSettings.showRuleChangeHistory) {
                List<RuleChangeDataManager.RuleChangeRecord> history = RuleChangeDataManager.INSTANCE.getLastChange(rule.name());
                if (!history.isEmpty()) {
                    for (RuleChangeDataManager.RuleChangeRecord lastChange : history) {
                        if (lastChange.isValid()) {
                            Messenger.m(source,
                                    "g  " + Translations.tr("igny.settings.record.operator", "Operator") + ": ", "w " + lastChange.sourceName(),
                                    "g  " + Translations.tr("igny.settings.record.change_time", "ChangeTime") + ": ", "w " + lastChange.getFormattedTime(),
                                    "g  " + Translations.tr("igny.settings.record.raw_value", "RawValue") + ": ", "w " +  objectToString(lastChange.rawValue()),
                                    "g  " + Translations.tr("igny.settings.record.new_value", "NewValue") + ": ", "w " + objectToString(lastChange.userInput())
                            );
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "addCarpetRule", at = @At(value = "TAIL"))
    private void addCarpetRule(CarpetRule<?> rule, CallbackInfo ci) {
        ClassUtil.getModIdFromStack("addCarpetRule", modId -> {
            List<String> rules = IGNYSettings.MOD_RULE_TREE.computeIfAbsent(modId, k -> new ArrayList<>());
            synchronized (rules) {
                rules.add(rule.name());
            }
        });
    }

    /**
     * @deprecated 这个方法的实现是不必要的，应该直接调用{@link Objects#toString(Object)}或{@link String#valueOf(Object)}，
     * 另外，在字符串拼接中，Java会自动将{@code null}引用替换为“null”字符串，无需显式转换
     */
    @Unique
    @Deprecated(forRemoval = true)
    private String objectToString(Object obj) {
        if (obj == null) return "null";
        return obj.toString();
    }

    @Unique
    public String getModIdByRuleName(String ruleName) {
        for (Map.Entry<String, List<String>> entry : IGNYSettings.MOD_RULE_TREE.entrySet()) {
            List<String> rules = entry.getValue();
            synchronized (rules) {
                if (rules.contains(ruleName)) {
                    return entry.getKey();
                }
            }
        }
        return "unknown";
    }

    @WrapOperation(method = {"setRule", "setDefault"}, at= @At(value = "INVOKE", target = "Lcarpet/api/settings/CarpetRule;set(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private <T> void onSetRuleValue(CarpetRule<T> instance, CommandSourceStack commandSourceStack, String s, Operation<Void> original){
        T rawValue = instance.value();
        original.call(instance, commandSourceStack, s);
        RuleObserver.handleChange(commandSourceStack, instance, rawValue, s);
        if (IGNYSettings.showRuleChangeHistory) {
            RuleChangeTracker.ruleChanged(commandSourceStack, instance, rawValue, s);
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
