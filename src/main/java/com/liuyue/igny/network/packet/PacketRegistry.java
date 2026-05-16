package com.liuyue.igny.network.packet;

//#if MC >= 12005
import com.liuyue.igny.network.packet.block.HighlightPayload;
import com.liuyue.igny.network.packet.block.RemoveHighlightPayload;
import com.liuyue.igny.network.packet.config.SyncCustomStackSizePayload;
import com.liuyue.igny.network.packet.config.SyncLinkedEnderChestPayload;
import com.liuyue.igny.network.packet.render.BoxPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//#endif

public class PacketRegistry {
    //#if MC >= 12005
    public static void s2c() {
        //#if MC >= 26.1
        //$$ PayloadTypeRegistry.clientboundPlay().register(HighlightPayload.TYPE, HighlightPayload.CODEC);
        //$$ PayloadTypeRegistry.clientboundPlay().register(RemoveHighlightPayload.TYPE, RemoveHighlightPayload.CODEC);
        //$$ PayloadTypeRegistry.clientboundPlay().register(SyncCustomStackSizePayload.TYPE, SyncCustomStackSizePayload.CODEC);
        //$$ PayloadTypeRegistry.clientboundPlay().register(BoxPayload.TYPE, BoxPayload.CODEC);
        //#else
        PayloadTypeRegistry.playS2C().register(HighlightPayload.TYPE, HighlightPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RemoveHighlightPayload.TYPE, RemoveHighlightPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncCustomStackSizePayload.TYPE, SyncCustomStackSizePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BoxPayload.TYPE, BoxPayload.CODEC);
        //#endif
    }

    public static void c2s() {
        //#if MC >= 26.1
        //$$ PayloadTypeRegistry.serverboundPlay().register(SyncLinkedEnderChestPayload.TYPE, SyncLinkedEnderChestPayload.CODEC);
        //#else
        PayloadTypeRegistry.playC2S().register(SyncLinkedEnderChestPayload.TYPE, SyncLinkedEnderChestPayload.CODEC);
        //#endif
    }
    //#endif
}
