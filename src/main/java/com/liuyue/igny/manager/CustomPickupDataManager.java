package com.liuyue.igny.manager;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.liuyue.igny.IGNYServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class CustomPickupDataManager {
    public static final String JSON_FILE_NAME = "custom_player_pickup.json";

    public enum Mode {
        @SerializedName("disabled") DISABLED,
        @SerializedName("whitelist") WHITELIST,
        @SerializedName("blacklist") BLACKLIST
    }

    public static class PlayerSetting {
        private Mode mode = Mode.DISABLED;
        private Set<String> items = new HashSet<>();

        public Mode getMode() { return mode; }
        public void setMode(Mode mode) { this.mode = mode; }
        public Set<String> getItems() { return items; }
        public void setItems(Collection<?> rawItems) {
            if (rawItems == null) {
                this.items = new HashSet<>();
                return;
            }
            this.items = rawItems.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .filter(s -> !s.isEmpty() && !"minecraft:air".equals(s))
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        }

        public boolean canPickUp(String itemId) {
            if (mode == Mode.DISABLED) return true;
            if ("minecraft:air".equals(itemId)) return false;
            boolean contains = items.contains(itemId.toLowerCase());
            return (mode == Mode.WHITELIST) == contains;
        }
    }

    private static final Map<String, PlayerSetting> settings = new ConcurrentHashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();
    private static MinecraftServer currentServer = null;

    public static void setServer(MinecraftServer server) {
        currentServer = server;
        settings.clear();
        load();
    }

    private static Path getJsonPath() {
        if (currentServer == null) {
            throw new IllegalStateException("Server not bound yet!");
        }
        return currentServer.getWorldPath(LevelResource.ROOT)
                .resolve(IGNYServer.MOD_ID)
                .resolve(JSON_FILE_NAME);
    }

    public static void load() {
        lock.lock();
        try {
            Path path = getJsonPath();
            if (!Files.exists(path)) {
                save();
                return;
            }

            String content = Files.readString(path);
            if (content.trim().isEmpty()) {
                save();
                return;
            }

            JsonObject root = JsonParser.parseString(content).getAsJsonObject();
            settings.clear();

            if (root.has("players") && root.get("players").isJsonObject()) {
                JsonObject players = root.getAsJsonObject("players");
                Gson gson = new Gson();
                for (String playerName : players.keySet()) {
                    PlayerSetting setting = gson.fromJson(players.get(playerName), PlayerSetting.class);
                    settings.put(playerName.toLowerCase(), setting);
                }
            }
        } catch (Exception e) {
            IGNYServer.LOGGER.error("Failed to load world pickup config: {}", e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public static void save() {
        lock.lock();
        try {
            Path path = getJsonPath();
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            JsonObject root = new JsonObject();
            JsonObject playersObj = new JsonObject();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            for (Map.Entry<String, PlayerSetting> entry : settings.entrySet()) {
                playersObj.add(entry.getKey(), gson.toJsonTree(entry.getValue()));
            }
            root.add("players", playersObj);
            Files.writeString(path, gson.toJson(root));
        } catch (IOException e) {
            IGNYServer.LOGGER.error("Failed to save world pickup config: {}", e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public static PlayerSetting getOrCreate(String playerName) {
        return settings.computeIfAbsent(playerName.toLowerCase(), k -> new PlayerSetting());
    }

    public static void updateAndSave(String playerName, PlayerSetting setting) {
        settings.put(playerName.toLowerCase(), setting);
        save();
    }

    public static boolean canPickUp(String playerName, String itemId) {
        PlayerSetting setting = settings.get(playerName.toLowerCase());
        return setting == null || setting.canPickUp(itemId);
    }
}