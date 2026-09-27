package com.liuyue.igny.utils;

import carpet.CarpetExtension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TranslationOwnership {
    private static CarpetExtension CURRENT;
    private static final Map<String, String> RULE_OWNER = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, String>> EXTENSION_LANG = new ConcurrentHashMap<>();

    private TranslationOwnership() {
    }

    public static void enter(CarpetExtension extension) {
        CURRENT = extension;
    }

    public static void exit() {
        CURRENT = null;
    }

    public static void recordRule(String ruleName) {
        if (CURRENT != null && ruleName != null && !ruleName.isEmpty()) {
            RULE_OWNER.put(ruleName, CURRENT.getClass().getName());
        }
    }

    public static void recordLang(CarpetExtension extension, Map<String, String> lang) {
        if (extension != null && lang != null && !lang.isEmpty()) {
            EXTENSION_LANG.put(extension.getClass().getName(), lang);
        }
    }

    public static Map<String, String> langOfRule(String ruleName) {
        String owner = RULE_OWNER.get(ruleName);
        return owner == null ? null : EXTENSION_LANG.get(owner);
    }
}
