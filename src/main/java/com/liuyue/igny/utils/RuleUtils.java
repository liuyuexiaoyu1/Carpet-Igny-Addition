package com.liuyue.igny.utils;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.IGNYSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RuleUtils {
    private static final List<ModContainer> extensionsMod = new ArrayList<>();
    //#if MC >= 12005
    public static Boolean canSoundSuppression(String name) {
        if ("false".equalsIgnoreCase(IGNYSettings.simpleSoundSuppression)) {
            return false;
        }
        if (name == null) {
            return false;
        }
        if ("true".equalsIgnoreCase(IGNYSettings.simpleSoundSuppression)) {
            return "声音抑制器".equals(name) || "soundSuppression".equalsIgnoreCase(name);
        }

        return Objects.equals(IGNYSettings.simpleSoundSuppression.toLowerCase(), name.toLowerCase());
    }
    //#endif

    public static Object getCarpetRulesValue(String modId, String ruleName) {
        if(IGNYServerMod.CARPET_ADDITION_MOD_IDS.contains(modId)){
            CarpetRule<?> carpetRule = CarpetServer.settingsManager.getCarpetRule(ruleName);
            if (carpetRule == null) {
                return false;
            }
            return carpetRule.value() == null ? false : carpetRule.value();
        }
        return false;
    }

    public static String getModIdFromClass(Class<?> clazz) {
        try {
            for (String modId : IGNYServerMod.CARPET_ADDITION_MOD_IDS) {
                ModContainer container = FabricLoader.getInstance().getModContainer(modId).orElse(null);
                if (container != null && !extensionsMod.contains(container)) {
                    extensionsMod.add(FabricLoader.getInstance().getModContainer(modId).orElse(null));
                }
            }
            URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
            Path classPath = Path.of(location.toURI());
            for (ModContainer mod : extensionsMod) {
                for (Path path : mod.getOrigin().getPaths()) {
                    String id = mod.getMetadata().getId();
                    String classFileName = classPath.getFileName().toString();
                    if (path.getFileName().toString().equals(classFileName) ||
                            id.equals(getModIdFromMetadata(classPath)) ||
                            classFileName.contains(id)) {
                        return id;
                    }
                }
            }
        } catch (Exception ignored) {}
        return "unknown";
    }

    private static String getModIdFromMetadata(Path jarPath) {
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
