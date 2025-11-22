package com.liuyue.igny.utils;


import com.liuyue.igny.IGNYSettings;
import carpet.api.settings.Rule;

import java.lang.reflect.Field;

public class CountRulesUtil {
    public static int countRules() {
        int ruleCount = 0;
        Field[] fields = IGNYSettings.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Rule.class)) {
                ruleCount++;
            }
        }
        return ruleCount;
    }
}
