package com.liuyue.igny;

import com.liuyue.igny.helper.inventory.LinkedContainer;
import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.network.packet.config.LinkedChestContentPayload;
import com.liuyue.igny.network.packet.config.SyncLinkedEnderChestPayload;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//#if < 1.20.5
/*$$import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;$$*/
//#endif

import java.util.ArrayList;
import java.util.List;

public class IGNYServerRegister {
    public static void register() {
        registerNetworkPackReceiver();
    }
    private static void registerNetworkPackReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(
                SyncLinkedEnderChestPayload.TYPE, //#replace < 1.20.5 ? IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID,
                (payload, context) -> { //#replace < 1.20.5 ? (server, player, impl, buf, sender) -> {
                    //#if < 1.20.5
                    //$$ String chestName = buf.readUtf();
                    //#else
                    String chestName = payload.key();
                    Player player = context.player();
                    //#endif
                    context.server().execute(() -> { //#replace < 1.20.5 ? server.execute(() -> {
                        if (chestName == null || chestName.isEmpty()) {
                            ((ViewingChest) player).igny$setLinkedKey(null);
                        } else {
                            ((ViewingChest) player).igny$setLinkedKey(chestName);
                        }
                        sendLinkedChestContent(player, chestName);
                    });
                }
        );
    }

    private static void sendLinkedChestContent(Player player, String key) {
        if (!(player instanceof ServerPlayer serverPlayer) || key == null || key.isEmpty()) {
            return;
        }
        if (!LinkedContainerManager.isRuleEnabled()) {
            return;
        }
        LinkedContainer container = LinkedContainerManager.peek(key);
        if (container == null || container.getActiveChests().isEmpty()) {
            return;
        }
        List<ItemStack> items = new ArrayList<>(container.getContainerSize());
        for (int i = 0; i < container.getContainerSize(); i++) {
            items.add(container.getItem(i).copy());
        }
        if (!ServerPlayNetworking.canSend(serverPlayer, LinkedChestContentPayload.TYPE)) return; //#replace < 1.20.5 ? if (!ServerPlayNetworking.canSend(serverPlayer, IGNYServer.LINKED_CHEST_CONTENT_PACKET_ID)) return;
        //#if < 1.20.5
        /*$$FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(key);
        buf.writeVarInt(items.size());
        for (ItemStack stack : items) {
            buf.writeItem(stack);
        }
        ServerPlayNetworking.send(serverPlayer, IGNYServer.LINKED_CHEST_CONTENT_PACKET_ID, buf);$$*/
        //#else
        ServerPlayNetworking.send(serverPlayer, new LinkedChestContentPayload(key, items));
        //#endif
    }
}
