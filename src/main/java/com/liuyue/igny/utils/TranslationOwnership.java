package com.liuyue.igny.utils;

import carpet.CarpetExtension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TranslationOwnership {
    private static final ThreadLocal<CarpetExtension> CURRENT = new ThreadLocal<>();
    private static final Map<String, String> MOD_ID_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> RULE_OWNER = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, String>> LANG_BY_MOD = new ConcurrentHashMap<>();

    private TranslationOwnership() {
    }

    public static void enter(CarpetExtension extension) {
        CURRENT.set(extension);
    }

    public static void exit() {
        CURRENT.remove();
    }

    public static String currentOwner() {
        CarpetExtension extension = CURRENT.get();
        return extension == null ? null : modIdOf(extension);
    }

    public static void recordRule(String ruleName) {
        recordRule(ruleName, currentOwner());
    }

    public static void recordRule(String ruleName, String modId) {
        if (modId != null && ruleName != null && !ruleName.isEmpty()) {
            RULE_OWNER.put(ruleName, modId);
        }
    }

    public static void recordLang(CarpetExtension extension, Map<String, String> lang) {
        if (extension != null) {
            recordLang(modIdOf(extension), lang);
        }
    }

    public static void recordLang(String modId, Map<String, String> lang) {
        if (modId != null && lang != null) {
            LANG_BY_MOD.put(modId, lang);
        }
    }

    public static String ownerOf(String ruleName) {
        return ruleName == null ? null : RULE_OWNER.get(ruleName);
    }

    public static Map<String, String> langOfRule(String ruleName) {
        String modId = ownerOf(ruleName);
        return modId == null ? null : LANG_BY_MOD.get(modId);
    }

    private static String modIdOf(CarpetExtension extension) {
        String className = extension.getClass().getName();
        String cached = MOD_ID_CACHE.get(className);
        if (cached != null) {
            return cached;
        }
        String[] holder = new String[1];
        ClassUtil.getModIdFromClass(extension.getClass(), id -> holder[0] = id);
        String modId = holder[0] == null ? "unknown" : holder[0];
        MOD_ID_CACHE.put(className, modId);
        return modId;
    }
}
