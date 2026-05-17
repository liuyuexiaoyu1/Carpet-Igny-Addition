package com.liuyue.igny.manager;

import com.google.gson.reflect.TypeToken;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.inventory.LinkedContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
//#if MC >= 12105
//$$ import net.minecraft.util.ProblemReporter;
//$$ import com.liuyue.igny.IGNYServer;
//#endif
//#if MC >= 12105
//$$ import java.util.Optional;
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
                //#if MC >= 12106
                //$$ var input = net.minecraft.world.level.storage.TagValueInput.create(
                //$$                      ProblemReporter.DISCARDING,
                //$$                      provider,
                //$$                      nbt
                //$$              );
                //$$              input.read("Items", net.minecraft.world.item.component.ItemContainerContents.CODEC).ifPresent(contents -> {
                //$$                  container.clearContent();
                //$$                  contents.copyInto(container.getItems());
                //$$                  // for (int i = 0; i < container.getContainerSize(); i++) {
                //$$                  //     container.setItem(i, contents.getStack(i));
                //$$                  // }
                //$$              });
                //#else
                //#if MC >= 12105
                //$$ Optional<ListTag> itemsTag = nbt.getList("Items");
                //#else
                ListTag itemsTag = nbt.getList("Items", Tag.TAG_COMPOUND);
                //#endif
                //#if MC >= 12005
                //#if MC >= 12105
                //$$ itemsTag.ifPresent(tag -> container.fromTag(tag, provider));
                //#else
                container.fromTag(itemsTag, provider);
                //#endif
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
            //$$ com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = net.minecraft.resources.RegistryOps.create(net.minecraft.nbt.NbtOps.INSTANCE, provider);
            //$$ java.util.List<net.minecraft.world.item.ItemStack> list = new java.util.ArrayList<>();
            //$$ for (int i = 0; i < container.getContainerSize(); i++) {
            //$$     list.add(container.getItem(i));
            //$$ }
            //$$ net.minecraft.world.item.component.ItemContainerContents contents = net.minecraft.world.item.component.ItemContainerContents.fromItems(list);
            //$$
            //$$ net.minecraft.world.item.component.ItemContainerContents.CODEC.encodeStart(ops, contents)
            //$$         .resultOrPartial(err -> IGNYServer.LOGGER.error("Encoding error: {}", err))
            //$$         .ifPresent(tag -> nbt.put("Items", tag));
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
    @Override protected SideRestraint getSideRestraint() { return SideRestraint.SERVER; }

    public static boolean isRuleEnabled() {
        return IGNYSettings.linkableEnderChest.equals(LinkedContainerSetting.FALSE);
    }

    public static boolean isRuleFully() {
        return IGNYSettings.linkableEnderChest.equals(LinkedContainerSetting.TRUE);
    }

    public enum LinkedContainerSetting {
        FALSE, ONLY_LINK, TRUE
    }
}
