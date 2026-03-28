package com.liuyue.igny.rule.callback;

import carpet.api.settings.CarpetRule;
import net.minecraft.commands.CommandSourceStack;

public interface RuleCallback<T> {
    void onChange(CommandSourceStack source, CarpetRule<T> rule, T oldValue, String newValue);
}
