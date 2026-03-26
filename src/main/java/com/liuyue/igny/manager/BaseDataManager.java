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

public abstract class BaseDataManager<T> {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
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
    }

    public void save() {
        Path path = getJsonPath();
        if (path == null) return;

        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(getCurrentData(), writer);
            }
        } catch (IOException e) {
            IGNYServer.LOGGER.error("Failed to save config: {}", getFileName(), e);
        }
    }
}