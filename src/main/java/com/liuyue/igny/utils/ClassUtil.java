package com.liuyue.igny.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.liuyue.igny.utils.deobfuscator.StackTraceDeobfuscator;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ClassUtil {

    public static void getModIdFromStack(String targetMethodName, boolean obfuscated, Consumer<String> callback) {
        StackTraceElement[] stack = obfuscated ? StackTraceDeobfuscator.deobfuscateStackTrace(Thread.currentThread().getStackTrace()) : Thread.currentThread().getStackTrace();
        CompletableFuture.supplyAsync(() -> {
            try {
                for (int i = 0; i < stack.length; i++) {
                    if (targetMethodName.equals(stack[i].getMethodName())) {
                        int callerIndex = i + 1;
                        if (callerIndex < stack.length) {
                            String clazzName = stack[callerIndex].getClassName();
                            return getModIdFromClass(Class.forName(clazzName));
                        }
                        break;
                    }
                }
            } catch (Exception ignored) {}
            return "unknown";
        }).thenAccept(callback);
    }

    public static void getModIdFromClass(Class<?> clazz, Consumer<String> callback) {
        CompletableFuture.supplyAsync(() -> getModIdFromClass(clazz)).thenAccept(callback);
    }

    private static String getModIdFromClass(Class<?> clazz) {
        try {
            CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
            if (codeSource == null) return "unknown";
            URL location = codeSource.getLocation();
            if (location == null) return "unknown";
            Path classPath = Path.of(location.toURI());
            String classPathStr = classPath.toString();
            for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
                ModOrigin origin = mod.getOrigin();
                String modId = mod.getMetadata().getId();
                if (origin.getKind() == ModOrigin.Kind.PATH) {
                    for (Path path : origin.getPaths()) {
                        if (classPathStr.contains(path.getFileName().toString())) {
                            return modId;
                        }
                    }
                } else if (origin.getKind() == ModOrigin.Kind.NESTED) {
                    String subLocation = origin.getParentSubLocation();
                    if (subLocation != null) {
                        String subJarName = subLocation.substring(subLocation.lastIndexOf('/') + 1);
                        if (classPathStr.contains(subJarName)) {
                            return modId;
                        }
                    }
                }
            }
            String idFromMeta = getModIdFromMetadata(classPath);
            if (!"unknown".equals(idFromMeta)) {
                return idFromMeta;
            }

        } catch (Exception ignored) {}
        return "unknown";
    }

    public static String getModIdFromMetadata(Path jarPath) {
        try {
            if (Files.isRegularFile(jarPath)) {
                try (FileSystem jarFs = FileSystems.newFileSystem(jarPath, (ClassLoader) null)) {
                    return extractIdFromJson(jarFs.getPath("fabric.mod.json"));
                }
            }
            else if (Files.isDirectory(jarPath)) {
                Path jsonPath = jarPath.resolve("fabric.mod.json");
                return extractIdFromJson(jsonPath);
            }
        } catch (Exception ignored) {}
        return "unknown";
    }

    private static String extractIdFromJson(Path jsonPath) {
        if (!Files.exists(jsonPath)) return "unknown";
        try (var reader = new InputStreamReader(Files.newInputStream(jsonPath))) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("id")) {
                return json.get("id").getAsString();
            }
        } catch (Exception ignored) {}
        return "unknown";
    }
}