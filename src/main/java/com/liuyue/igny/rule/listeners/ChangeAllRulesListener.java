package com.liuyue.igny.rule.listeners;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.InvalidRuleValueException;
import carpet.api.settings.RuleHelper;
import carpet.api.settings.SettingsManager;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.mixins.carpet.features.enableAllRules.SettingsManagerInvoker;
import com.liuyue.igny.rule.RuleListener;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ChangeAllRulesListener implements RuleListener<Boolean> {
    private final Map<String, String> previousValues = new HashMap<>();

    @Override
    public void onChanged(@Nullable CommandSourceStack source, Boolean value) {
        if (value) {
            enableAll(source);
        } else {
            restoreAll(source);
        }
    }

    private void enableAll(@Nullable CommandSourceStack source) {
        previousValues.clear();
        forEachManager(manager -> enableAllForManager(manager, source));
    }

    private void restoreAll(@Nullable CommandSourceStack source) {
        forEachManager(manager -> restoreAllForManager(manager, source));
        previousValues.clear();
    }

    private void enableAllForManager(SettingsManager manager, @Nullable CommandSourceStack source) {
        for (CarpetRule<?> rule : manager.getCarpetRules()) {
            if (isSkippedRule(rule)) continue;

            String target = pickValue(rule);
            if (target == null) continue;

            previousValues.put(ruleKey(manager, rule), RuleHelper.toRuleString(rule.value()));
            setRuleWithDefaultDisabled(manager, rule, target, source);
        }
    }

    private void restoreAllForManager(SettingsManager manager, @Nullable CommandSourceStack source) {
        for (CarpetRule<?> rule : manager.getCarpetRules()) {
            String oldValue = previousValues.get(ruleKey(manager, rule));
            if (oldValue == null) continue;

            setRuleWithDefaultDisabled(manager, rule, oldValue, source);
        }
    }

    private void setRuleWithDefaultDisabled(SettingsManager manager,
                                            CarpetRule<?> rule,
                                            String value,
                                            @Nullable CommandSourceStack source) {
        boolean original = IGNYSettings.TWO_CHANGED_RULE_VALUE_SET_DEFAULT.value();
        try {
            setRuleValue(IGNYSettings.TWO_CHANGED_RULE_VALUE_SET_DEFAULT.getCarpetRule(), false);
            ((SettingsManagerInvoker) manager).invokeSetRule(source, rule, value);
        } finally {
            setRuleValue(IGNYSettings.TWO_CHANGED_RULE_VALUE_SET_DEFAULT.getCarpetRule(), original);
        }
    }

    private boolean isSkippedRule(CarpetRule<?> rule) {
        return IGNYSettings.ALL_RULES_ENABLED.name().equals(rule.name())
                || IGNYSettings.TWO_CHANGED_RULE_VALUE_SET_DEFAULT.name().equals(rule.name());
    }

    private String ruleKey(SettingsManager manager, CarpetRule<?> rule) {
        return manager.identifier() + ":" + rule.name();
    }

    private <T> void setRuleValue(CarpetRule<T> rule, T value) {
        try {
            rule.set(null, value);
        } catch (InvalidRuleValueException ignored) {
        }
    }

    private void forEachManager(Consumer<SettingsManager> consumer) {
        if (CarpetServer.settingsManager != null) {
            consumer.accept(CarpetServer.settingsManager);
        }
        for (CarpetExtension ext : CarpetServer.extensions) {
            SettingsManager sm = ext.extensionSettingsManager();
            if (sm != null) {
                consumer.accept(sm);
            }
        }
    }

    private String pickValue(CarpetRule<?> rule) {
        Class<?> type = rule.type();
        String defaultValue = RuleHelper.toRuleString(rule.defaultValue());
        if (type == boolean.class || type == Boolean.class) {
            return defaultValue.equalsIgnoreCase("true") ? "false" : "true";
        }
        if (type == String.class || type.isEnum()) {
            List<String> options = new ArrayList<>(rule.suggestions());
            if (defaultValue.equalsIgnoreCase("on")) {
                String off = findIgnoreCase(options, "off");
                if (off != null) return off;
            }
            if (defaultValue.equalsIgnoreCase("true")) {
                String falseValue = findIgnoreCase(options, "false");
                if (falseValue != null) return falseValue;
            }
            String on = findIgnoreCase(options, "on");
            if (on != null) return on;

            String trueValue = findIgnoreCase(options, "true");
            if (trueValue != null) return trueValue;

            options.removeIf(opt -> opt.equals(defaultValue));
            if (options.isEmpty()) return null;
            return options.get((int) (Math.random() * options.size()));
        }
        return null;
    }

    @Nullable
    private static String findIgnoreCase(List<String> options, String target) {
        for (String opt : options) {
            if (opt.equalsIgnoreCase(target)) return opt;
        }
        return null;
    }
}