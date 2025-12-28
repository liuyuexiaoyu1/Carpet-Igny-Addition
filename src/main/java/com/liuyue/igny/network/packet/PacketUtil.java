package com.liuyue.igny.network.packet;

//#if MC >= 12005
import com.liuyue.igny.IGNYServer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
//#endif

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
}
