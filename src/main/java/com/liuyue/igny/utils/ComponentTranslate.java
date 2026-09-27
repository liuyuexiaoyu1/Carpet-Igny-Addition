package com.liuyue.igny.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class ComponentTranslate {
    private static final Gson GSON = new Gson();

    public static Map<String, String> getTranslationFromResourcePath(String lang) {
        Optional<InputStream> langFile = FabricLoader.getInstance().getModContainer("carpet-igny-addition")
                .flatMap(c -> c.findPath("assets/carpet-igny-addition/lang/" + lang + ".json"))
                .map(p -> {
                    try {
                        return Files.newInputStream(p);
                    } catch (IOException e) {
                        return null;
                    }
                });

        if (langFile.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            String jsonData = IOUtils.toString(langFile.get(), StandardCharsets.UTF_8);
            return GSON.fromJson(jsonData, new TypeToken<Map<String, String>>() {}.getType());
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}
