package com.liuyue.igny.rule;

import carpet.api.settings.CarpetRule;
import net.minecraft.commands.CommandSourceStack;

public interface RuleCallback<T> {
    void onChange(CommandSourceStack source, CarpetRule<T> rule, T oldValue, String newValue);
}
