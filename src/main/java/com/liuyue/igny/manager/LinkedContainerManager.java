package com.liuyue.igny.manager;

import com.google.gson.reflect.TypeToken;
import com.liuyue.igny.helper.inventory.LinkedContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
//#if MC >= 12109
//$$ import net.minecraft.util.ProblemReporter;
//$$ import com.liuyue.igny.IGNYServer;
//#endif

public class LinkedContainerManager extends BaseDataManager<Map<String, String>> {
    public static final LinkedContainerManager INSTANCE = new LinkedContainerManager();
    private final Map<String, LinkedContainer> containers = new HashMap<>();

    @Override protected String getFileName() { return "linked_chests.json"; }
    @Override protected Type getDataType() { return new TypeToken<Map<String, String>>(){}.getType(); }
    @Override public Map<String, String> getDefaultData() { return new HashMap<>(); }

    @Override
    protected void applyData(Map<String, String> data) {
        if (server == null) return;
        HolderLookup.Provider provider = server.registryAccess();
        data.forEach((key, snbt) -> {
            LinkedContainer container = get(key);
            try {
                //#if MC >= 12105
                //$$ CompoundTag nbt = TagParser.parseCompoundFully(snbt);
                //#else
                CompoundTag nbt = TagParser.parseTag(snbt);
                //#endif
                //#if MC >= 12105
                //$$ var input = net.minecraft.world.level.storage.TagValueInput.create(
                //$$                      ProblemReporter.DISCARDING,
                //$$                      provider,
                //$$                      nbt
                //$$              );
                //$$              input.list("Items", net.minecraft.world.item.ItemStack.CODEC).ifPresent(typedList -> {
                //$$                  container.clearContent();
                //$$                  java.util.List<net.minecraft.world.item.ItemStack> stacks = typedList.stream().toList();
                //$$                  for (int i = 0; i < Math.min(stacks.size(), container.getContainerSize()); i++) {
                //$$                      container.setItem(i, stacks.get(i));
                //$$                  }
                //$$              });
                //#else
                ListTag itemsTag = nbt.getList("Items", Tag.TAG_COMPOUND);
                //#if MC >= 12005
                container.fromTag(itemsTag, provider);
                //#else
                //$$ container.fromTag(itemsTag);
                //#endif
                //#endif
                containers.put(key, container);
            } catch (Exception ignored) {}
        });
    }

    @Override
    public Map<String, String> getCurrentData() {
        Map<String, String> data = new HashMap<>();
        if (server == null) return data;

        HolderLookup.Provider provider = server.registryAccess();
        containers.forEach((key, container) -> {
            CompoundTag nbt = new CompoundTag();
            //#if MC >= 12105
            //$$ net.minecraft.nbt.NbtOps ops = net.minecraft.nbt.NbtOps.INSTANCE;
            //$$          java.util.List<net.minecraft.world.item.ItemStack> list = new java.util.ArrayList<>();
            //$$          for (int i = 0; i < container.getContainerSize(); i++) {
            //$$              list.add(container.getItem(i));
            //$$          }
            //$$          net.minecraft.world.item.ItemStack.CODEC.listOf().encodeStart(ops, list)
            //$$                  .resultOrPartial(err -> IGNYServer.LOGGER.error("Encoding error: {}", err))
            //$$                  .ifPresent(tag -> nbt.put("Items", tag));
            //#else
            //#if MC >= 12005
            nbt.put("Items", container.createTag(provider));
            //#else
            //$$ nbt.put("Items", container.createTag());
            //#endif
            //#endif
            data.put(key, nbt.toString());
        });
        return data;
    }

    public static LinkedContainer get(String key) {
        return INSTANCE.containers.computeIfAbsent(key, LinkedContainer::new);
    }

    @Override protected StorageScope getScope() { return StorageScope.WORLD; }
    @Override protected SideRestraint getSideRestraint() { return SideRestraint.COMMON; }
}