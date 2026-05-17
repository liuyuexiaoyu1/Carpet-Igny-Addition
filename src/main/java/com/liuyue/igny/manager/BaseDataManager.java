package com.liuyue.igny.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public abstract class BaseDataManager<T> {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected MinecraftServer server;

    protected abstract String getFileName();
    protected abstract Type getDataType();
    public abstract T getDefaultData();
    protected abstract void applyData(T data);
    public abstract T getCurrentData();

    protected abstract StorageScope getScope();
    protected abstract SideRestraint getSideRestraint();

    @SuppressWarnings("all")
    protected boolean isEffective() {
        EnvType currentEnv = FabricLoader.getInstance().getEnvironmentType();
        SideRestraint restraint = getSideRestraint();

        if (restraint == SideRestraint.CLIENT) return currentEnv == EnvType.CLIENT;
        if (restraint == SideRestraint.SERVER) return true;
        return true;
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

    public void setServer(MinecraftServer server) {
        this.server = server;
        if (server != null) {
            load();
        } else if (getScope() == StorageScope.WORLD) {
            clearInMemoryCache();
        }
    }

    public void load() {
        if (!isEffective()) return;

        Path path = getJsonPath();
        if (path == null) return;
        if (!Files.exists(path)) {
            applyData(getDefaultData());
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            T data = GSON.fromJson(reader, getDataType());
            applyData(data != null ? data : getDefaultData());
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

        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(getCurrentData(), writer);
            }
        } catch (IOException e) {
            IGNYServer.LOGGER.error("Failed to save config [{}]: {}", getFileName(), e.getMessage());
        }
    }

    public void clearInMemoryCache() {
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