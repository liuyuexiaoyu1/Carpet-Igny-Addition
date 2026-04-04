package com.liuyue.igny.rule;

import carpet.api.settings.CarpetRule;
import com.liuyue.igny.rule.annotation.ObservedRule;
import com.liuyue.igny.rule.callback.RuleCallback;
import net.minecraft.commands.CommandSourceStack;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class RuleObserver {
    private static final Map<String, RuleCallback<?>> callbacks = new HashMap<>();

    public static void init(Class<?> settingsClass) throws Exception {
        for (Field field : settingsClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ObservedRule.class)) continue;
            ObservedRule anno = field.getAnnotation(ObservedRule.class);
            Class<? extends RuleCallback<?>> clazz = anno.value();
            var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            RuleCallback<?> instance = constructor.newInstance();
            callbacks.put(field.getName(), instance);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void handleChange(CommandSourceStack source, CarpetRule<T> rule, T oldVal, T newVal) {
        RuleCallback<T> callback = (RuleCallback<T>) callbacks.get(rule.name());
        if (callback != null) {
            callback.onChange(source, rule, oldVal, newVal);
        }
    }
}
