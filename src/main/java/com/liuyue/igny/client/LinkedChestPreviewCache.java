package com.liuyue.igny.client;

//? < 1.20.5 ? import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.helper.inventory.LinkedContainer;
import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.network.packet.config.SyncLinkedEnderChestPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.component.DataComponents; //? >= 1.20.5
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//#if < 1.20.5
/*$$import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;$$*/
//#endif

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LinkedChestPreviewCache {
    private static final long REFRESH_INTERVAL_MS = 500L;
    private static final long PREVIEW_TTL_MS = 100L;
    private static final Map<String, List<ItemStack>> CONTENT = new ConcurrentHashMap<>();
    private static final Map<String, Long> REQUESTED_AT = new ConcurrentHashMap<>();
    private static String previewKey;
    private static long previewStamp;

    private LinkedChestPreviewCache() {}

    public static void beginPreview(ItemStack stack) {
        previewKey = keyOf(stack);
        previewStamp = System.currentTimeMillis();
        if (previewKey != null) {
            request(previewKey);
        }
    }

    public static void endPreview() {
        previewKey = null;
    }

    public static PlayerEnderChestContainer previewContainer() {
        String key = previewKey;
        if (key == null || System.currentTimeMillis() - previewStamp > PREVIEW_TTL_MS) {
            return null;
        }
        List<ItemStack> items = CONTENT.get(key);
        if (items == null) {
            return null;
        }
        LinkedContainer container = new LinkedContainer(key);
        int size = Math.min(items.size(), container.getContainerSize());
        for (int i = 0; i < size; i++) {
            container.setItem(i, items.get(i));
        }
        return container;
    }

    public static void accept(String key, List<ItemStack> items) {
        CONTENT.put(key, items);
    }

    public static void clear() {
        CONTENT.clear();
        REQUESTED_AT.clear();
        previewKey = null;
    }

    private static String keyOf(ItemStack stack) {
        if (!stack.is(Items.ENDER_CHEST)) {
            return null;
        }
        //#if >= 1.20.5
        Component name = stack.get(DataComponents.CUSTOM_NAME);
        //#else
        //$$Component name = stack.hasCustomHoverName() ? stack.getHoverName() : null;
        //#endif
        return name == null ? null : name.getString();
    }

    private static void request(String key) {
        if (!LinkedContainerManager.isRuleEnabled()) {
            return;
        }
        long now = System.currentTimeMillis();
        Long last = REQUESTED_AT.get(key);
        if (last != null && now - last < REFRESH_INTERVAL_MS) {
            return;
        }
        REQUESTED_AT.put(key, now);
        if (!ClientPlayNetworking.canSend(SyncLinkedEnderChestPayload.TYPE)) return; //#replace < 1.20.5 ? if (!ClientPlayNetworking.canSend(IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID)) return;
        //#if < 1.20.5
        /*$$FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(key);
        ClientPlayNetworking.send(IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID, buf);$$*/
        //#else
        ClientPlayNetworking.send(new SyncLinkedEnderChestPayload(key));
        //#endif
    }
}
