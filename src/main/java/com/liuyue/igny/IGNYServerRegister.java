package com.liuyue.igny;

import com.liuyue.igny.network.packet.config.SyncLinkedEnderChestPayload;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.player.Player;
//?< 1.20.5 ? import com.liuyue.igny.IGNYServer;

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
                    });
                }
        );
    }
}
