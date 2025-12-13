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
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RuleChangeDataManager {
    private static final String TABLE_NAME = "rule_changes";
    private static final String JSON_FILE_NAME = "rule_changes.json";

    private static MinecraftServer server;
    private static Connection connection;
    private static boolean useDatabase = false;

    private static final Map<String, RuleChangeRecord> inMemoryCache = new ConcurrentHashMap<>();
    private static final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void setServer(MinecraftServer server) {
        closeConnection();
        inMemoryCache.clear();

        RuleChangeDataManager.server = server;
        if (server == null) {
            return;
        }

        connect();

        File jsonFile = getJsonFile();
        boolean jsonFileExists = jsonFile.exists() && jsonFile.length() > 0;

        if (useDatabase && jsonFileExists) {
            if (mergeJsonToDatabase(jsonFile)) {
                if (!jsonFile.delete()) {
                    String worldName = server.getWorldData().getLevelName();
                    IGNYServer.LOGGER.warn("Merged data from JSON to database but failed to delete rule_changes.json for world '{}'", worldName);
                }
            } else {
                String worldName = server.getWorldData().getLevelName();
                IGNYServer.LOGGER.error("Failed to merge rule_changes.json with database for world '{}'", worldName);
            }
        } else if (!useDatabase && jsonFileExists) {
            loadFromJson();
        } else if (useDatabase) {
            IGNYServer.LOGGER.debug("Database connected successfully, no JSON file found for migration");
        }
    }

    private static boolean mergeJsonToDatabase(File jsonFile) {
        try (FileReader reader = new FileReader(jsonFile)) {
            Type mapType = new TypeToken<Map<String, RuleChangeRecord>>() {}.getType();
            Map<String, RuleChangeRecord> jsonRecords = GSON.fromJson(reader, mapType);

            if (jsonRecords == null || jsonRecords.isEmpty()) {
                return true;
            }

            Map<String, RuleChangeRecord> dbRecords = getAllFromDatabase();

            connection.setAutoCommit(false);

            try {
                for (Map.Entry<String, RuleChangeRecord> entry : jsonRecords.entrySet()) {
                    String ruleName = entry.getKey();
                    RuleChangeRecord jsonRecord = entry.getValue();

                    if (jsonRecord == null || !jsonRecord.isValid()) {
                        IGNYServer.LOGGER.warn("Skipping invalid JSON record for rule: {}", ruleName);
                        continue;
                    }

                    if (dbRecords.containsKey(ruleName)) {
                        updateDatabaseRecord(ruleName, jsonRecord);
                        IGNYServer.LOGGER.debug("Updated database record for rule '{}' from JSON", ruleName);
                    } else {
                        insertDatabaseRecord(jsonRecord);
                        IGNYServer.LOGGER.debug("Inserted new record for rule '{}' from JSON", ruleName);
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                IGNYServer.LOGGER.error("Failed to merge data, rolling back transaction", e);
                connection.rollback();
                return false;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (Exception e) {
            IGNYServer.LOGGER.error("Error during JSON-to-database merge", e);
            return false;
        }
    }

    private static void updateDatabaseRecord(String ruleName, RuleChangeRecord record) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE " + TABLE_NAME + " SET raw_value = ?, current_value = ?, source_name = ?, timestamp = ?, formatted_time = ? WHERE rule_name = ?")) {
            stmt.setString(1, serialize(record.rawValue));
            stmt.setString(2, serialize(record.currentValue));
            stmt.setString(3, record.sourceName);
            stmt.setLong(4, record.timestamp);
            stmt.setString(5, record.formattedTime);
            stmt.setString(6, ruleName);
            stmt.executeUpdate();
        }
    }

    private static void insertDatabaseRecord(RuleChangeRecord record) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO " + TABLE_NAME + " (rule_name, raw_value, current_value, source_name, timestamp, formatted_time) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, record.ruleName);
            stmt.setString(2, serialize(record.rawValue));
            stmt.setString(3, serialize(record.currentValue));
            stmt.setString(4, record.sourceName);
            stmt.setLong(5, record.timestamp);
            stmt.setString(6, record.formattedTime);
            stmt.executeUpdate();
        }
    }

    private static void connect() {
        try {
            File dbFile = server.getWorldPath(LevelResource.ROOT)
                    .resolve(IGNYServerMod.getModId())
                    .resolve("rule_changes.db")
                    .toFile();

            if (!dbFile.getParentFile().exists() && !dbFile.getParentFile().mkdirs()) {
                throw new IOException("Failed to create directory for SQLite database");
            }

            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);
            createTable();
            useDatabase = true;
        } catch (Exception e) {
            IGNYServer.LOGGER.warn("Failed to connect to SQLite database, falling back to JSON storage: {}", e.getMessage());
            connection = null;
            useDatabase = false;
        }
    }

    private static void createTable() throws SQLException {
        if (connection == null) return;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS %s (
                    rule_name TEXT PRIMARY KEY,
                    raw_value TEXT NOT NULL,
                    current_value TEXT NOT NULL,
                    source_name TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    formatted_time TEXT NOT NULL
                );
                """.formatted(TABLE_NAME));
        }
    }

    private static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                IGNYServer.LOGGER.warn("Failed to close database connection", e);
            } finally {
                connection = null;
                useDatabase = false;
            }
        }
    }

    public static void recordRuleChange(String ruleName, Object originalValue,
                                        String userInput, String sourceName, long timestamp) {
        if (useDatabase) {
            recordToDatabase(ruleName, originalValue, userInput, sourceName, timestamp);
        } else {
            recordToJson(ruleName, originalValue, userInput, sourceName, timestamp);
        }
    }

    public static Optional<RuleChangeRecord> getLastChange(String ruleName) {
        if (useDatabase) {
            return getFromDatabase(ruleName);
        } else {
            cacheLock.readLock().lock();
            try {
                return Optional.ofNullable(inMemoryCache.get(ruleName));
            } finally {
                cacheLock.readLock().unlock();
            }
        }
    }

    public static Map<String, RuleChangeRecord> getAllChanges() {
        if (useDatabase) {
            return getAllFromDatabase();
        } else {
            cacheLock.readLock().lock();
            try {
                return new HashMap<>(inMemoryCache);
            } finally {
                cacheLock.readLock().unlock();
            }
        }
    }

    public static void clearHistory(String ruleName) {
        if (useDatabase) {
            clearFromDatabase(ruleName);
        } else {
            cacheLock.writeLock().lock();
            try {
                inMemoryCache.remove(ruleName);
                saveToJson();
            } finally {
                cacheLock.writeLock().unlock();
            }
        }
    }

    public static void clearAllHistory() {
        if (useDatabase) {
            clearAllFromDatabase();
        } else {
            cacheLock.writeLock().lock();
            try {
                inMemoryCache.clear();
                saveToJson();
            } finally {
                cacheLock.writeLock().unlock();
            }
        }
    }

    private static void recordToDatabase(String ruleName, Object originalValue,
                                         Object userInput, String sourceName, long timestamp) {
        if (connection == null) return;
        String formattedTime = formatTimestamp(timestamp);
        String rawJson = serialize(originalValue);
        String currentJson = serialize(userInput);

        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO " + TABLE_NAME + " (rule_name, raw_value, current_value, source_name, timestamp, formatted_time) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, ruleName);
            stmt.setString(2, rawJson);
            stmt.setString(3, currentJson);
            stmt.setString(4, sourceName);
            stmt.setLong(5, timestamp);
            stmt.setString(6, formattedTime);
            stmt.executeUpdate();
        } catch (SQLException e) {
            IGNYServer.LOGGER.error("Failed to record rule change to DB: {}", e.getMessage());
        }
    }

    private static Optional<RuleChangeRecord> getFromDatabase(String ruleName) {
        if (connection == null) return Optional.empty();
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM " + TABLE_NAME + " WHERE rule_name = ?")) {
            stmt.setString(1, ruleName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new RuleChangeRecord(
                        rs.getString("rule_name"),
                        deserialize(rs.getString("raw_value")),
                        deserialize(rs.getString("current_value")),
                        rs.getString("source_name"),
                        rs.getLong("timestamp")
                ));
            }
        } catch (SQLException e) {
            IGNYServer.LOGGER.error("Failed to get rule change from DB: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private static Map<String, RuleChangeRecord> getAllFromDatabase() {
        Map<String, RuleChangeRecord> result = new HashMap<>();
        if (connection == null) return result;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME)) {
            while (rs.next()) {
                RuleChangeRecord record = new RuleChangeRecord(
                        rs.getString("rule_name"),
                        deserialize(rs.getString("raw_value")),
                        deserialize(rs.getString("current_value")),
                        rs.getString("source_name"),
                        rs.getLong("timestamp")
                );
                result.put(record.ruleName, record);
            }
        } catch (SQLException e) {
            IGNYServer.LOGGER.error("Failed to get all rule changes from DB: {}", e.getMessage());
        }
        return result;
    }

    private static void clearFromDatabase(String ruleName) {
        if (connection == null) return;
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM " + TABLE_NAME + " WHERE rule_name = ?")) {
            stmt.setString(1, ruleName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            IGNYServer.LOGGER.error("Failed to clear rule history from DB: {}", e.getMessage());
        }
    }

    private static void clearAllFromDatabase() {
        if (connection == null) return;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM " + TABLE_NAME);
        } catch (SQLException e) {
            IGNYServer.LOGGER.error("Failed to clear all rule history from DB: {}", e.getMessage());
        }
    }

    private static void loadFromJson() {
        try {
            File jsonFile = getJsonFile();
            if (!jsonFile.exists()) {
                return;
            }

            try (FileReader reader = new FileReader(jsonFile)) {
                Type mapType = new TypeToken<Map<String, RuleChangeRecord>>(){}.getType();
                Map<String, RuleChangeRecord> loaded = GSON.fromJson(reader, mapType);
                if (loaded != null) {
                    inMemoryCache.clear();
                    inMemoryCache.putAll(loaded);
                }
            }
        } catch (Exception e) {
            IGNYServer.LOGGER.error("Failed to load rule changes from JSON: {}", e.getMessage());
        }
    }

    private static void saveToJson() {
        try {
            File jsonFile = getJsonFile();
            jsonFile.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(jsonFile)) {
                GSON.toJson(inMemoryCache, writer);
            }
        } catch (Exception e) {
            IGNYServer.LOGGER.error("Failed to save rule changes to JSON: {}", e.getMessage());
        }
    }

    private static void recordToJson(String ruleName, Object originalValue,
                                     String userInput, String sourceName, long timestamp) {
        RuleChangeRecord record = new RuleChangeRecord(ruleName, originalValue, userInput, sourceName, timestamp);
        cacheLock.writeLock().lock();
        try {
            inMemoryCache.put(ruleName, record);
            saveToJson();
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    private static File getJsonFile() {
        return server.getWorldPath(LevelResource.ROOT)
                .resolve(IGNYServerMod.getModId())
                .resolve(JSON_FILE_NAME)
                .toFile();
    }

    private static String serialize(Object obj) {
        return GSON.toJson(obj);
    }

    private static Object deserialize(String json) {
        return GSON.fromJson(json, Object.class);
    }

    private static String formatTimestamp(long timestamp) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(timestamp));
    }

    public static class RuleChangeRecord {
        public final String ruleName;
        public final Object rawValue;
        public final Object currentValue;
        public final String sourceName;
        public final long timestamp;
        public final String formattedTime;

        public RuleChangeRecord(String ruleName, Object rawValue, Object currentValue,
                                String sourceName, long timestamp) {
            this.ruleName = ruleName;
            this.rawValue = rawValue;
            this.currentValue = currentValue;
            this.sourceName = sourceName;
            this.timestamp = timestamp;
            this.formattedTime = formatTimestamp(timestamp);
        }

        public boolean isValid() {
            return ruleName != null && !ruleName.isEmpty() &&
                    rawValue != null &&
                    currentValue != null &&
                    sourceName != null && !sourceName.isEmpty();
        }

        @SuppressWarnings("unused")
        private RuleChangeRecord() {
            this.ruleName = "";
            this.rawValue = null;
            this.currentValue = null;
            this.sourceName = "";
            this.timestamp = 0;
            this.formattedTime = "";
        }
    }
}