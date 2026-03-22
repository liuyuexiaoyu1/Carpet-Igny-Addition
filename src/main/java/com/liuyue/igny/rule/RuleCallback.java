package com.liuyue.igny.rule;

import carpet.api.settings.CarpetRule;

public interface RuleCallback<T> {
    void onChange(CarpetRule<T> rule, T oldValue, String newValue);
}
