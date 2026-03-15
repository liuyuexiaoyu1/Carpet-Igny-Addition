package com.liuyue.igny.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ClassUtil {
    public static void getModIdFromStack(String targetMethodName, Consumer<String> callback) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
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

    public static void getModIdFromClassAsync(Class<?> clazz, Consumer<String> callback) {
        CompletableFuture.supplyAsync(() -> getModIdFromClass(clazz)).thenAccept(callback);
    }

    private static String getModIdFromClass(Class<?> clazz) {
        try {
            URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
            if (location == null) return "unknown";

            Path classPath = Path.of(location.toURI());
            for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
                String id = mod.getMetadata().getId();
                for (Path path : mod.getOrigin().getPaths()) {
                    String classFileName = classPath.getFileName().toString();
                    String modFileName = path.getFileName().toString();

                    if (modFileName.equals(classFileName) || id.equals(getModIdFromMetadata(classPath))) {
                        return id;
                    }
                }
            }
        } catch (Exception ignored) {}
        return "unknown";
    }

    public static String getModIdFromMetadata(Path jarPath) {
        try {
            if (Files.isRegularFile(jarPath) && jarPath.toString().endsWith(".jar")) {
                try (FileSystem jarFs = FileSystems.newFileSystem(jarPath, (ClassLoader) null)) {
                    Path jsonPath = jarFs.getPath("fabric.mod.json");
                    if (Files.exists(jsonPath)) {
                        return extractIdFromJson(jsonPath);
                    }
                }
            }
        } catch (Exception ignored) {}
        return "unknown";
    }

    private static String extractIdFromJson(Path jsonPath) {
        try (var reader = new InputStreamReader(Files.newInputStream(jsonPath))) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("id")) {
                return json.get("id").getAsString();
            }
        } catch (Exception ignored) {}
        return "carpet";
    }
}
