package com.liuyue.igny.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.IGNYServerMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseDataManager<T> {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "IGNY-IO-" + getFileName());
        thread.setDaemon(true);
        return thread;
    });
    protected MinecraftServer server;

    protected abstract String getFileName();
    protected abstract Type getDataType();
    public abstract T getDefaultData();
    protected abstract void applyData(T data);
    public abstract T getCurrentData();

    public void setServer(MinecraftServer server) {
        this.server = server;
        if (server != null) {
            load();
        } else {
            clearInMemoryCache();
        }
    }

    public void clearInMemoryCache() {
        applyData(getDefaultData());
    }

    protected Path getJsonPath() {
        if (server == null) return null;
        return server.getWorldPath(LevelResource.ROOT)
                .resolve(IGNYServerMod.getModId())
                .resolve(getFileName());
    }

    public void load() {
        CompletableFuture.runAsync(() -> {
            Path path = getJsonPath();
            if (path == null || !Files.exists(path)) {
                applyData(getDefaultData());
                return;
            }

            try (Reader reader = Files.newBufferedReader(path)) {
                T data = GSON.fromJson(reader, getDataType());
                applyData(data != null ? data : getDefaultData());
            } catch (Exception e) {
                IGNYServer.LOGGER.error("Failed to load config: {}", getFileName(), e);
                applyData(getDefaultData());
            }
        }, ioExecutor);
    }

    public void save() {
        T dataToSave = getCurrentData();
        Path path = getJsonPath();
        if (path == null || dataToSave == null) {
            CompletableFuture.completedFuture(null);
            return;
        }
        CompletableFuture.runAsync(() -> {
            try {
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                Path tempPath = path.resolveSibling(getFileName() + ".tmp");
                try (Writer writer = Files.newBufferedWriter(tempPath)) {
                    GSON.toJson(dataToSave, writer);
                }
                Files.deleteIfExists(path);
                Files.move(tempPath, path);
            } catch (IOException e) {
                IGNYServer.LOGGER.error("Failed to save config: {}", getFileName(), e);
            }
        }, ioExecutor);
    }
}