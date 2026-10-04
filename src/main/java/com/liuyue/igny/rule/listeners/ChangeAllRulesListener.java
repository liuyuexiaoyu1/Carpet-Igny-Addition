package com.liuyue.igny.rule.listeners;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
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

        forEachManager(manager -> {
            for (CarpetRule<?> rule : manager.getCarpetRules()) {
                if (IGNYSettings.ALL_RULES_ENABLED.name().equals(rule.name())) continue;

                String target = pickValue(rule);
                if (target == null) continue;
                String key = manager.identifier() + ":" + rule.name();
                previousValues.put(key, RuleHelper.toRuleString(rule.value()));

                ((SettingsManagerInvoker) manager).invokeSetRule(source, rule, target);
            }
        });
    }

    private void restoreAll(@Nullable CommandSourceStack source) {
        forEachManager(manager -> {
            for (CarpetRule<?> rule : manager.getCarpetRules()) {
                String key = manager.identifier() + ":" + rule.name();
                String oldValue = previousValues.get(key);
                if (oldValue == null) continue;

                ((SettingsManagerInvoker) manager).invokeSetRule(source, rule, oldValue);
            }
        });
        previousValues.clear();
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
            return "true";
        }

        if (type == String.class || type.isEnum()) {
            List<String> options = new ArrayList<>(rule.suggestions());

            for (String opt : options) {
                if (opt.equalsIgnoreCase("on")) return opt;
            }
            for (String opt : options) {
                if (opt.equalsIgnoreCase("true")) return opt;
            }

            options.removeIf(opt -> opt.equals(defaultValue));
            if (options.isEmpty()) return null;
            return options.get((int) (Math.random() * options.size()));
        }

        return null;
    }
}