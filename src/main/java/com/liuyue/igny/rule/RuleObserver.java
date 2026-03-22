package com.liuyue.igny.rule;

import carpet.api.settings.CarpetRule;
import com.liuyue.igny.rule.annotation.ObservedRule;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class RuleObserver {
    private static final Map<String, RuleCallback<?>> callbacks = new HashMap<>();

    public static void init(Class<?> settingsClass) throws Exception {
        for (Field field : settingsClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ObservedRule.class)) continue;
            ObservedRule anno = field.getAnnotation(ObservedRule.class);
            Class<? extends RuleCallback<?>> clazz = anno.callback();
            var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            RuleCallback<?> instance = constructor.newInstance();
            callbacks.put(field.getName(), instance);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void handleChange(CarpetRule<T> rule, T oldVal, String newVal) {
        RuleCallback<T> callback = (RuleCallback<T>) callbacks.get(rule.name());
        if (callback != null) {
            callback.onChange(rule, oldVal, newVal);
        }
    }
}
