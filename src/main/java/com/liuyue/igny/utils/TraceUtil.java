// The MIT License (MIT)
//
// Copyright (c) 2020 comp500
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.liuyue.igny.utils;

import com.liuyue.igny.utils.deobfuscator.StackTraceDeobfuscator;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TraceUtil {
    private static final Object MIXIN_ACCESSOR;

    static {
        Object accessor = null;
        try {
            try {
                @SuppressWarnings("all")
                Method getMixins = ClassInfo.class.getDeclaredMethod("getMixins");
                getMixins.setAccessible(true);
                accessor = getMixins;
            } catch (NoSuchMethodException e) {
                Field mixinsField = ClassInfo.class.getDeclaredField("mixins");
                mixinsField.setAccessible(true);
                accessor = mixinsField;
            }
        } catch (Exception ignored) {}
        MIXIN_ACCESSOR = accessor;
    }

    public static void printTrace(StackTraceElement[] stackTrace, StringBuilder builder) {
        if (stackTrace == null || stackTrace.length == 0) return;

        StackTraceElement[] deobfStackTrace = StackTraceDeobfuscator.deobfuscateStackTrace(stackTrace);

        builder.append("\nMixins in Stacktrace:");

        Map<String, String> classMap = new LinkedHashMap<>();
        for (int i = 0; i < stackTrace.length; i++) {
            classMap.put(stackTrace[i].getClassName(), deobfStackTrace[i].getClassName());
        }

        boolean found = false;
        try {
            for (Map.Entry<String, String> entry : classMap.entrySet()) {
                String className = entry.getKey();
                String deobfClassName = entry.getValue();

                ClassInfo classInfo = ClassInfo.fromCache(className);
                if (classInfo == null || MIXIN_ACCESSOR == null) continue;

                Object mixinSetObj = (MIXIN_ACCESSOR instanceof Method m)
                        ? m.invoke(classInfo)
                        : ((Field) MIXIN_ACCESSOR).get(classInfo);

                if (mixinSetObj instanceof Set<?> mixinSet && !mixinSet.isEmpty()) {
                    found = true;

                    builder.append("\n\t").append(className);
                    if (!className.equals(deobfClassName)) {
                        builder.append(" (").append(deobfClassName).append(")");
                    }
                    builder.append(":");

                    for (Object obj : mixinSet) {
                        if (obj instanceof IMixinInfo info) {
                            builder.append("\n\t\t").append(info.getClassName())
                                    .append(" (").append(info.getConfig().getName()).append(")");
                        }
                    }
                }
            }
            if (!found) builder.append(" None found");
        } catch (Exception e) {
            builder.append(" Failed to find Mixin metadata: ").append(e.getMessage());
        }
    }
}