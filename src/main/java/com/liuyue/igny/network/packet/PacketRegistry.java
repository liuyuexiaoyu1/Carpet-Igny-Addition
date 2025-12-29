package com.liuyue.igny.network.packet;

//#if MC >= 12005
import com.liuyue.igny.network.packet.block.HighlightPayload;
import com.liuyue.igny.network.packet.block.RemoveHighlightPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//#endif

public class PacketRegistry {
    //#if MC >= 12005
    public static void s2c() {
        PayloadTypeRegistry.playS2C().register(HighlightPayload.TYPE, HighlightPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RemoveHighlightPayload.TYPE, RemoveHighlightPayload.CODEC);
    }
    //#endif
}
