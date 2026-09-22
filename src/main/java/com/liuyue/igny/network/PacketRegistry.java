package com.liuyue.igny.network;

//#if >= 1.20.5
import com.liuyue.igny.network.packet.block.HighlightPayload;
import com.liuyue.igny.network.packet.block.RemoveHighlightPayload;
import com.liuyue.igny.network.packet.config.SyncCustomStackSizePayload;
import com.liuyue.igny.network.packet.config.SyncLinkedEnderChestPayload;
import com.liuyue.igny.network.packet.render.BoxPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//#endif

public class PacketRegistry {
    //#if >= 1.20.5
    public static void s2c() {
        //#if >= 26.1
        /*$$PayloadTypeRegistry.clientboundPlay().register(HighlightPayload.TYPE, HighlightPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(RemoveHighlightPayload.TYPE, RemoveHighlightPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncCustomStackSizePayload.TYPE, SyncCustomStackSizePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BoxPayload.TYPE, BoxPayload.CODEC);$$*/
        //#else
        PayloadTypeRegistry.playS2C().register(HighlightPayload.TYPE, HighlightPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RemoveHighlightPayload.TYPE, RemoveHighlightPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncCustomStackSizePayload.TYPE, SyncCustomStackSizePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BoxPayload.TYPE, BoxPayload.CODEC);
        //#endif
    }

    public static void c2s() {
        PayloadTypeRegistry.playC2S().register(SyncLinkedEnderChestPayload.TYPE, SyncLinkedEnderChestPayload.CODEC); //#replace >= 26.1 ? PayloadTypeRegistry.serverboundPlay().register(SyncLinkedEnderChestPayload.TYPE, SyncLinkedEnderChestPayload.CODEC);
    }
    //#endif
}
