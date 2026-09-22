package com.liuyue.igny.manager;

import com.google.gson.reflect.TypeToken;
import com.liuyue.igny.IGNYServer;
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
//?>= 1.21.5 ? import net.minecraft.util.ProblemReporter;
//?>= 1.21.5 ? import java.util.Optional;

public class LinkedContainerManager extends BaseDataManager<Map<String, String>> {
    public static final LinkedContainerManager INSTANCE = new LinkedContainerManager();
    private final Map<String, LinkedContainer> containers = new HashMap<>();

    public static LinkedContainer get(String key) {
        return INSTANCE.containers.computeIfAbsent(key, LinkedContainer::new);
    }

    public static boolean isRuleEnabled() {
        return IGNYSettings.LINKEABLE_ENDER_CHEST.value() != LinkedContainerSetting.FALSE;
    }

    public static boolean isRuleFully() {
        return IGNYSettings.LINKEABLE_ENDER_CHEST.value() == LinkedContainerSetting.TRUE;
    }

    @Override
    protected String getFileName() {
        return "linked_chests.json";
    }

    @Override
    protected int getCurrentVersion() {
        return 1;
    }

    @Override
    protected Type getDataType() {
        return new TypeToken<Map<String, String>>() {
        }.getType();
    }

    @Override
    public Map<String, String> getDefaultData() {
        return new HashMap<>();
    }

    @Override
    protected void applyData(Map<String, String> data) {
        if (server == null) return;
        HolderLookup.Provider provider = server.registryAccess();
        data.forEach((key, snbt) -> {
            LinkedContainer container = get(key);
            try {
                CompoundTag nbt = TagParser.parseTag(snbt); //#replace >= 1.21.5 ? CompoundTag nbt = TagParser.parseCompoundFully(snbt);
                //#if >= 1.21.6
                /*$$var input = net.minecraft.world.level.storage.TagValueInput.create(
                                     ProblemReporter.DISCARDING,
                                     provider,
                                     nbt
                             );
                             input.read("Items", net.minecraft.world.item.component.ItemContainerContents.CODEC).ifPresent(contents -> {
                                 container.clearContent();
                                 contents.copyInto(container.getItems());
                                 // for (int i = 0; i < container.getContainerSize(); i++) {
                                 //     container.setItem(i, contents.getStack(i));
                                 // }
                });$$*/
                //#else
                ListTag itemsTag = nbt.getList("Items", Tag.TAG_COMPOUND); //#replace >= 1.21.5 ? Optional<ListTag> itemsTag = nbt.getList("Items");
                //#if >= 1.20.5
                container.fromTag(itemsTag, provider); //#replace >= 1.21.5 ? itemsTag.ifPresent(tag -> container.fromTag(tag, provider));
                //#else
                //$$ container.fromTag(itemsTag);
                //#endif
                //#endif
                containers.put(key, container);
            } catch (Exception e) {
                IGNYServer.LOGGER.warn("Failed to load linked chest data for key '{}': {}", key, e.getMessage());
            }
        });
    }

    @Override
    public Map<String, String> getCurrentData() {
        Map<String, String> data = new HashMap<>();
        if (server == null) return data;

        HolderLookup.Provider provider = server.registryAccess();
        containers.forEach((key, container) -> {
            CompoundTag nbt = new CompoundTag();
            Tag items = container.igny$createItemsTag(provider);
            if (items != null) nbt.put("Items", items);
            data.put(key, nbt.toString());
        });
        return data;
    }

    @Override
    protected StorageScope getScope() {
        return StorageScope.WORLD;
    }

    @Override
    protected SideRestraint getSideRestraint() {
        return SideRestraint.SERVER;
    }

    @Override
    public void clear() {
        containers.clear();
    }

    public enum LinkedContainerSetting {
        FALSE, ONLY_LINK, TRUE
    }
}
