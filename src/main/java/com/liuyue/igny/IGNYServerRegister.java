package com.liuyue.igny;

import com.liuyue.igny.network.packet.config.SyncLinkedEnderChestPayload;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.player.Player;
//#if MC < 12005
//$$ import com.liuyue.igny.IGNYServer;
//#endif

public class IGNYServerRegister {
    public static void register() {
        registerNetworkPackReceiver();
    }

    private static void registerNetworkPackReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(
                //#if MC < 12005
                //$$ IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID,
                //#else
                SyncLinkedEnderChestPayload.TYPE,
                //#endif
                //#if MC < 12005
                //$$ (server, player, impl, buf, sender) -> server.execute(() -> {
                //#else
                (payload, context) -> context.server().execute(() -> {
                    //#endif
                    //#if MC < 12005
                    //$$ String chestName = buf.readUtf();
                    //#else
                    String chestName = payload.key();
                    Player player = context.player();
                    //#endif
                    if (chestName == null || chestName.isEmpty()) {
                        ((ViewingChest) player).igny$setLinkedKey(null);
                    } else {
                        ((ViewingChest) player).igny$setLinkedKey(chestName);
                    }
                })
        );
    }
}
