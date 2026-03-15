package com.liuyue.igny.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.IGNYServerMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RuleChangeDataManager {
    private static final String JSON_FILE_NAME = "rule_changes.json";

    private static MinecraftServer server;
    private static final Map<String, List<RuleChangeRecord>> inMemoryCache = new ConcurrentHashMap<>();
    private static final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void setServer(MinecraftServer server) {
        cacheLock.writeLock().lock();
        try {
            inMemoryCache.clear();
            RuleChangeDataManager.server = server;

            if (server != null) {
                loadFromJson();
            }
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    public static void recordRuleChange(String ruleName, Object originalValue,
                                        String userInput, String sourceName, long timestamp) {
        RuleChangeRecord record = new RuleChangeRecord(originalValue, userInput, sourceName, timestamp);

        cacheLock.writeLock().lock();
        try {
            List<RuleChangeRecord> history = inMemoryCache.computeIfAbsent(ruleName, k -> new ArrayList<>());
            history.addLast(record);
            if (history.size() > 3) {
                history.removeFirst();
            }
            saveToJson();
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    public static List<RuleChangeRecord> getLastChange(String ruleName) {
        cacheLock.readLock().lock();
        try {
            return new ArrayList<>(inMemoryCache.getOrDefault(ruleName, Collections.emptyList()));
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    @SuppressWarnings("unused")
    public static Map<String, List<RuleChangeRecord>> getAllChanges() {
        cacheLock.readLock().lock();
        try {
            return new HashMap<>(inMemoryCache);
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    @SuppressWarnings("unused")
    public static void clearHistory(String ruleName) {
        cacheLock.writeLock().lock();
        try {
            if (inMemoryCache.remove(ruleName) != null) {
                saveToJson();
            }
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    @SuppressWarnings("unused")
    public static void clearAllHistory() {
        cacheLock.writeLock().lock();
        try {
            inMemoryCache.clear();
            saveToJson();
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    private static void loadFromJson() {
        File jsonFile = getJsonFile();
        if (!jsonFile.exists() || jsonFile.length() == 0) {
            return;
        }

        try (FileReader reader = new FileReader(jsonFile)) {
            Type mapType = new TypeToken<Map<String, List<RuleChangeRecord>>>() {}.getType();
            Map<String, List<RuleChangeRecord>> loaded = GSON.fromJson(reader, mapType);
            if (loaded != null) {
                inMemoryCache.putAll(loaded);
            }
        } catch (Exception e) {
            IGNYServer.LOGGER.error("Failed to load rule changes from JSON: {}", e.getMessage());
        }
    }

    private static void saveToJson() {
        if (server == null) return;

        try {
            File jsonFile = getJsonFile();
            File parent = jsonFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IOException("Failed to create directories for: " + jsonFile.getAbsolutePath());
            }

            try (FileWriter writer = new FileWriter(jsonFile)) {
                GSON.toJson(inMemoryCache, writer);
            }
        } catch (Exception e) {
            IGNYServer.LOGGER.error("Failed to save rule changes to JSON: {}", e.getMessage());
        }
    }

    private static File getJsonFile() {
        return server.getWorldPath(LevelResource.ROOT)
                .resolve(IGNYServerMod.getModId())
                .resolve(JSON_FILE_NAME)
                .toFile();
    }

    private static String formatTimestamp(long timestamp) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(timestamp));
    }

    public static class RuleChangeRecord {
        public final Object rawValue;
        public final Object userInput;
        public final String sourceName;
        public final long timestamp;
        public final String formattedTime;

        public RuleChangeRecord(Object rawValue, Object userInput,
                                String sourceName, long timestamp) {
            this.rawValue = rawValue;
            this.userInput = userInput;
            this.sourceName = sourceName;
            this.timestamp = timestamp;
            this.formattedTime = formatTimestamp(timestamp);
        }

        public boolean isValid() {
            return rawValue != null && userInput != null && sourceName != null && !sourceName.isEmpty();
        }

        @SuppressWarnings("unused")
        private RuleChangeRecord() {
            this.rawValue = null;
            this.userInput = null;
            this.sourceName = "";
            this.timestamp = 0;
            this.formattedTime = "";
        }
    }
}