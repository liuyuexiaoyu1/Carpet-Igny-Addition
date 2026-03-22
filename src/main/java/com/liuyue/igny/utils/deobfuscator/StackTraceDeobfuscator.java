package com.liuyue.igny.utils.deobfuscator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.utils.deobfuscator.mapping.MappingReader;
import com.liuyue.igny.utils.deobfuscator.mapping.TinyMappingV2Reader;
import com.liuyue.igny.utils.deobfuscator.yarn.OnlineMappingProvider;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Copy from Carpet-TIS-Addition
 * 此文件以LGPL-3.0协议开源
 */

public class StackTraceDeobfuscator {
    static Map<String, String> MAPPING = Maps.newHashMap();
    static boolean fetchedMapping = false;
    static String MAPPING_VERSION = "no mapping";

    public static synchronized void fetchMapping() {
        if (!fetchedMapping) {
            OnlineMappingProvider.getMapping();
            fetchedMapping = true;
        }
    }

    public static boolean loadMappings(InputStream inputStream, String mappingVersion) {
        Map<String, String> mappings;
        MappingReader mappingReader = new TinyMappingV2Reader();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            mappings = mappingReader.readMapping(reader);
        } catch (Exception e) {
            IGNYServer.LOGGER.error("Fail to load mapping {}", mappingVersion, e);
            return false;
        }

        MAPPING = mappings;
        MAPPING_VERSION = mappingVersion;
        return true;
    }

    public static StackTraceElement[] deobfuscateStackTrace(StackTraceElement[] stackTraceElements, @Nullable String ignoreClassPath) {
        List<StackTraceElement> list = Lists.newArrayList();
        for (StackTraceElement element : stackTraceElements) {
            String remappedClass = MAPPING.get(element.getClassName());
            String remappedMethod = MAPPING.get(element.getMethodName());
            StackTraceElement newElement = new StackTraceElement(
                    remappedClass != null ? remappedClass : element.getClassName(),
                    remappedMethod != null ? remappedMethod : element.getMethodName(),
                    remappedClass != null ? getFileName(remappedClass) : element.getFileName(),
                    element.getLineNumber()
            );
            list.add(newElement);
            if (ignoreClassPath != null && newElement.getClassName().startsWith(ignoreClassPath)) {
                list.clear();
            }
        }
        return list.toArray(new StackTraceElement[0]);
    }

    @SuppressWarnings("unused")
    public static StackTraceElement[] deobfuscateStackTrace(StackTraceElement[] stackTraceElements) {
        return deobfuscateStackTrace(stackTraceElements, null);
    }

    private static String getFileName(String className) {
        if (className.isEmpty()) {
            return className;
        }
        return className.substring(className.lastIndexOf('.') + 1).split("\\$", 2)[0] + ".java";
    }
}