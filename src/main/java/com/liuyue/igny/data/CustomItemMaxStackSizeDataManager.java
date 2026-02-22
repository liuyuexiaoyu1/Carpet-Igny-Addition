package com.liuyue.igny.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.network.packet.PacketUtil;
import com.mojang.brigadier.StringReader;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class CustomItemMaxStackSizeDataManager {
    //#if MC >= 12006
    public static final String JSON_FILE_NAME = "custom_item_max_stack_size.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Map<String, Integer> customStacks = new HashMap<>();

    private static final List<StackRule> runtimeRules = new CopyOnWriteArrayList<>();
    private static MinecraftServer currentServer;

    private record StackRule(String pattern, Predicate<ItemStack> predicate, int size) {}

    public static void setServer(MinecraftServer server) {
        currentServer = server;
        customStacks.clear();
        load();
        rebuildRuntimeRules(CommandBuildContext.simple(server.registryAccess(), server.getWorldData().enabledFeatures()));
    }

    public static void setClient(Minecraft client) {
        customStacks.clear();
        if (client.getConnection() != null) rebuildRuntimeRules(CommandBuildContext.simple(client.getConnection().registryAccess(), client.getConnection().enabledFeatures()));
    }

    public static int getCustomStackSize(ItemStack stack) {
        for (StackRule rule : runtimeRules) {
            if (rule.predicate.test(stack)) {
                return rule.size;
            }
        }
        return -1;
    }

    public static void set(String pattern, int count, CommandBuildContext context) {
        customStacks.put(pattern, count);
        rebuildRuntimeRules(context);
        save();
        if (currentServer != null) {
            currentServer.getPlayerList().getPlayers().forEach(PacketUtil::sendCustomStackSizeToClient);
        }
    }

    public static void remove(String pattern, CommandBuildContext context) {
        customStacks.remove(pattern);
        rebuildRuntimeRules(context);
        save();
        if (currentServer != null) {
            currentServer.getPlayerList().getPlayers().forEach(PacketUtil::sendCustomStackSizeToClient);
        }
    }

    public static void clear() {
        customStacks.clear();
        runtimeRules.clear();
        save();
    }

    private static void rebuildRuntimeRules(CommandBuildContext context) {
        List<StackRule> newRules = new ArrayList<>();
        customStacks.forEach((pattern, count) -> {
            try {
                var result = ItemPredicateArgument.itemPredicate(context).parse(new StringReader(pattern));
                newRules.add(new StackRule(pattern, result, count));
            } catch (Exception e) {
                ResourceLocation rl = ResourceLocation.tryParse(pattern);
                if (rl != null && BuiltInRegistries.ITEM.containsKey(rl)) {
                    Item item = BuiltInRegistries.ITEM.
                            //#if MC >= 12102
                            //$$ getValue(rl);
                            //#else
                            get(rl);
                            //#endif
                    newRules.add(new StackRule(pattern, stack -> stack.is(item), count));
                } else {
                    IGNYServer.LOGGER.error("Invalid pattern received from server: {}", pattern);
                }
                IGNYServer.LOGGER.error("Failed to parse pattern: {} \n {}", pattern, e);
            }
        });
        newRules.sort((a, b) -> {
            boolean aIsTag = a.pattern.startsWith("#") || a.pattern.startsWith("*");
            boolean bIsTag = b.pattern.startsWith("#") || b.pattern.startsWith("*");
            String aId = aIsTag ? a.pattern.substring(1) : a.pattern.split("\\[")[0].split("\\{")[0];
            String bId = bIsTag ? a.pattern.substring(1) : a.pattern.split("\\[")[0].split("\\{")[0];
            boolean aExists = !aIsTag && BuiltInRegistries.ITEM.containsKey(ResourceLocation.tryParse(aId));
            boolean bExists = !bIsTag && BuiltInRegistries.ITEM.containsKey(ResourceLocation.tryParse(bId));
            if (aExists != bExists) return aExists ? -1 : 1;
            if (aIsTag != bIsTag) return aIsTag ? 1 : -1;
            if (a.pattern.length() != b.pattern.length()) return b.pattern.length() - a.pattern.length();
            return a.pattern.compareTo(b.pattern);
        });

        runtimeRules.clear();
        runtimeRules.addAll(newRules);
    }

    public static void load() {
        if (currentServer == null) {
            IGNYServer.LOGGER.info("Client mode: Skipping local file load, waiting for server data.");
            return;
        }
        assert getJsonPath() != null;
        File file = getJsonPath().toFile();
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            Map<String, Integer> loaded = GSON.fromJson(reader, new TypeToken<Map<String, Integer>>(){}.getType());
            if (loaded != null) customStacks = loaded;
        } catch (IOException e) {
            IGNYServer.LOGGER.error("Failed to load custom item stack config", e);
        }
    }

    public static void save() {
        if (currentServer == null) {
            IGNYServer.LOGGER.info("Client mode: Skipping local file load, waiting for server data.");
            return;
        }
        assert getJsonPath() != null;
        File file = getJsonPath().toFile();
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(customStacks, writer);
        } catch (IOException e) {
            IGNYServer.LOGGER.error("Failed to save custom item stack config", e);
        }
    }

    public static void clientUpdateData(Map<String, Integer> newData) {
        customStacks = new HashMap<>(newData);
        var minecraft = Minecraft.getInstance();
        if (minecraft.level != null && minecraft.getConnection() != null) {
            CommandBuildContext context = CommandBuildContext.simple(
                    minecraft.getConnection().registryAccess(),
                    minecraft.getConnection().enabledFeatures()
            );
            rebuildRuntimeRules(context);
        }
    }

    public static Map<String, Integer> getCustomStacks() {
        return Collections.unmodifiableMap(customStacks);
    }

    private static Path getJsonPath() {
        if (currentServer == null) return null;
        return currentServer.getWorldPath(LevelResource.ROOT).resolve(IGNYServer.MOD_ID).resolve(JSON_FILE_NAME);
    }
    //#endif
}