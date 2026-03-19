package com.liuyue.igny.network.packet;

//#if MC >= 12005
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
//#endif
import net.minecraft.server.level.ServerPlayer;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.manager.CustomItemMaxStackSizeDataManager;
import com.liuyue.igny.network.packet.config.SyncCustomStackSizePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//#if MC < 12005
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//$$ import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//#endif

import java.util.Map;

public class PacketUtil {
    //#if MC >= 12005
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> createId(String path) {
        ResourceLocation identifier =
                //#if MC < 12100
                //$$ new ResourceLocation(IGNYServer.MOD_ID, path);
                //#else
                ResourceLocation.fromNamespaceAndPath(IGNYServer.MOD_ID, path);
                //#endif
        return new CustomPacketPayload.Type<>(identifier);
    }
    //#endif

    //#if MC >= 12006
    public static void sendCustomStackSizeToClient(ServerPlayer player) {
        Map<String, Integer> data = CustomItemMaxStackSizeDataManager.getCustomStacks();
        if (data.isEmpty()) return;

        if (ServerPlayNetworking.canSend(player,
                //#if MC >= 12005
                SyncCustomStackSizePayload.TYPE
                //#else
                //$$ IGNYServer.SYNC_STACK_SIZE_PACKET_ID
                //#endif
        )) {
            //#if MC < 12005
            //$$ FriendlyByteBuf buf = PacketByteBufs.create();
            //$$ buf.writeVarInt(data.size());
            //$$ data.forEach((id, count) -> {
            //$$     buf.writeUtf(id);
            //$$     buf.writeVarInt(count);
            //$$ });
            //#endif

            ServerPlayNetworking.send(
                    player,
                    //#if MC >= 12005
                    new SyncCustomStackSizePayload(data)
                    //#else
                    //$$ IGNYServer.SYNC_STACK_SIZE_PACKET_ID,
                    //$$ buf
                    //#endif
            );
        }
    }
    //#endif
}
