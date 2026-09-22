package com.liuyue.igny.network;

//#if >= 1.20.5
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
//#endif
import net.minecraft.server.level.ServerPlayer;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.manager.CustomItemMaxStackSizeDataManager;
import com.liuyue.igny.network.packet.config.SyncCustomStackSizePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//#if < 1.20.5
/*$$import net.minecraft.network.FriendlyByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;$$*/
//#endif

import java.util.Map;

public class PacketUtil {
    //#if >= 1.20.5
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> createId(String path) {
        ResourceLocation identifier =
                ResourceLocation.fromNamespaceAndPath(IGNYServer.MOD_ID, path); //#replace < 1.21.0 ? new ResourceLocation(IGNYServer.MOD_ID, path);
        return new CustomPacketPayload.Type<>(identifier);
    }
    //#endif

    //#if >= 1.20.6
    public static void sendCustomStackSizeToClient(ServerPlayer player) {
        Map<String, Integer> data = CustomItemMaxStackSizeDataManager.INSTANCE.getCurrentData();
        if (data.isEmpty()) return;

        if (ServerPlayNetworking.canSend(player,
                SyncCustomStackSizePayload.TYPE //#replace < 1.20.5 ? IGNYServer.SYNC_STACK_SIZE_PACKET_ID
        )) {
            //#if < 1.20.5
            /*$$FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeVarInt(data.size());
            data.forEach((id, count) -> {
                buf.writeUtf(id);
                buf.writeVarInt(count);
            });$$*/
            //#endif

            ServerPlayNetworking.send(
                    player,
                    //#if >= 1.20.5
                    new SyncCustomStackSizePayload(data)
                    //#else
                    /*$$IGNYServer.SYNC_STACK_SIZE_PACKET_ID,
                    buf$$*/
                    //#endif
            );
        }
    }
    //#endif
}
