package com.liuyue.igny.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.IGNYServerMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseDataManager<T> {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String DATA_VERSION_KEY = "data_version";
    public static final String DATA_KEY = "data";

    private static final Set<BaseDataManager<?>> managers = ConcurrentHashMap.newKeySet();

    protected MinecraftServer server;

    BaseDataManager() {
        managers.add(this);
    }

    public static void saveAll() {
        managers.forEach(BaseDataManager::save);
    }

    public static void clearAll() {
        managers.forEach(BaseDataManager::clear);
    }

    public static void setServerAll(MinecraftServer server) {
        managers.forEach(m -> m.setServer(server));
    }

    protected abstract String getFileName();

    protected abstract Type getDataType();

    public abstract T getDefaultData();

    protected abstract void applyData(T data);

    public abstract T getCurrentData();

    protected abstract StorageScope getScope();

    protected abstract SideRestraint getSideRestraint();

    protected int getCurrentVersion() {
        return 1;
    }

    protected JsonObject upgradeData(JsonObject json, int oldVersion) {
        return json;
    }

    protected List<String> getLegacyFileNames() {
        return Collections.emptyList();
    }

    protected List<Path> getLegacyPaths() {
        return Collections.emptyList();
    }

    @SuppressWarnings("all")
    protected boolean isEffective() {
        EnvType currentEnv = FabricLoader.getInstance().getEnvironmentType();
        SideRestraint restraint = getSideRestraint();

        return restraint == SideRestraint.CLIENT ? currentEnv == EnvType.CLIENT : true;
    }

    protected Path getJsonPath() {
        if (!isEffective()) return null;
        String modId = IGNYServerMod.getModId();
        if (getScope() == StorageScope.GLOBAL) {
            return FabricLoader.getInstance().getConfigDir().resolve(modId).resolve(getFileName());
        }
        if (server == null) return null;
        return server.getWorldPath(LevelResource.ROOT).resolve(modId).resolve(getFileName());
    }

    private void migrateFromLegacy() {
        Path target = getJsonPath();
        if (target == null || Files.exists(target)) return;

        Path parent = target.getParent();
        for (String legacyName : getLegacyFileNames()) {
            Path legacy = parent != null ? parent.resolve(legacyName) : Path.of(legacyName);
            if (Files.exists(legacy)) {
                migrateFile(legacy, target);
                return;
            }
        }
        for (Path legacy : getLegacyPaths()) {
            if (Files.exists(legacy)) {
                migrateFile(legacy, target);
                return;
            }
        }
    }

    private void migrateFile(Path legacy, Path target) {
        try {
            Files.createDirectories(target.getParent());
            Files.move(legacy, target, StandardCopyOption.REPLACE_EXISTING);
            IGNYServer.LOGGER.info("Migrated data file [{}] -> [{}]", legacy, target);
        } catch (IOException e) {
            IGNYServer.LOGGER.error("Failed to migrate data file [{}] -> [{}]: {}",
                    legacy, target, e.getMessage());
        }
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
        if (server != null) {
            load();
        } else if (getScope() == StorageScope.WORLD) {
            reset();
        }
    }

    public void load() {
        if (!isEffective()) return;

        migrateFromLegacy();

        Path path = getJsonPath();
        if (path == null) return;
        if (!Files.exists(path)) {
            applyData(getDefaultData());
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            int oldVersion;
            JsonElement dataElement;
            if (json.has(DATA_KEY)) {
                oldVersion = json.has(DATA_VERSION_KEY) ? json.get(DATA_VERSION_KEY).getAsInt() : 0;
            } else {
                oldVersion = 0;
                JsonObject wrapped = new JsonObject();
                wrapped.add(DATA_KEY, json);
                json = wrapped;
            }
            dataElement = json.get(DATA_KEY);

            if (oldVersion < getCurrentVersion()) {
                json = upgradeData(json, oldVersion);
                if (json.has(DATA_KEY)) {
                    dataElement = json.get(DATA_KEY);
                }
            }

            T data = GSON.fromJson(dataElement, getDataType());
            applyData(data != null ? data : getDefaultData());

            if (oldVersion < getCurrentVersion()) {
                save();
            }
        } catch (Exception e) {
            IGNYServer.LOGGER.error("Failed to load config [{}] for side [{}]: {}",
                    getFileName(), getSideRestraint(), e.getMessage());
            applyData(getDefaultData());
        }
    }

    public void save() {
        if (!isEffective()) return;

        Path path = getJsonPath();
        if (path == null) return;

        JsonObject wrapper = new JsonObject();
        wrapper.addProperty(DATA_VERSION_KEY, getCurrentVersion());
        wrapper.add(DATA_KEY, GSON.toJsonTree(getCurrentData()));

        Path tempPath = null;
        Path backupPath = null;
        boolean hasOriginalFile = Files.exists(path);

        try {
            Files.createDirectories(path.getParent());
            tempPath = Files.createTempFile(path.getParent(),
                    com.google.common.io.Files.getNameWithoutExtension(path.getFileName().toString()) + "-",
                    ".tmp");
            try (Writer writer = Files.newBufferedWriter(tempPath)) {
                GSON.toJson(wrapper, writer);
            }
            if (hasOriginalFile) {
                backupPath = path.resolveSibling(path.getFileName() + ".bak");
                Files.deleteIfExists(backupPath);
                Files.move(path, backupPath, StandardCopyOption.REPLACE_EXISTING);
            }
            try {
                Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                if (backupPath != null && Files.exists(backupPath)) {
                    Files.move(backupPath, path, StandardCopyOption.REPLACE_EXISTING);
                }
                throw e;
            }
            if (backupPath != null) {
                Files.deleteIfExists(backupPath);
            }

        } catch (IOException e) {
            if (hasOriginalFile && backupPath != null && Files.exists(backupPath)) {
                try {
                    Files.move(backupPath, path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException recoveryError) {
                    e.addSuppressed(recoveryError);
                }
            }
            if (tempPath != null) {
                try {
                    Files.deleteIfExists(tempPath);
                } catch (IOException cleanupError) {
                    e.addSuppressed(cleanupError);
                }
            }
            IGNYServer.LOGGER.error("Failed to save config [{}]: {}", getFileName(), e.getMessage());
        }
    }

    public abstract void clear();

    public void reset() {
        applyData(getDefaultData());
    }

    protected enum StorageScope {
        GLOBAL,
        WORLD
    }

    protected enum SideRestraint {
        CLIENT,
        SERVER
    }
}
