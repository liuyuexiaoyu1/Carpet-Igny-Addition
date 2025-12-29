package com.liuyue.igny.client;

import com.liuyue.igny.client.renderer.highlightBlocks.HighlightBlocksRenderer;
import com.liuyue.igny.network.packet.block.HighlightPayload;
import com.liuyue.igny.network.packet.block.RemoveHighlightPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//#if MC < 12005
//$$ import com.liuyue.igny.IGNYServer;
//$$ import net.minecraft.core.BlockPos;
//#else
//$$ import com.liuyue.igny.network.packet.block.HighlightPayload;
//#endif

public class IGNYClientRegister {
    public static void register() {
        registerNetworkPackReceiver();
    }

    private static void registerNetworkPackReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(
                //#if MC < 12005
                //$$ IGNYServer.HIGHLIGHT_PACKET_ID,
                //#else
                HighlightPayload.TYPE,
                //#endif
                //#if MC < 12005
                //$$ (client, handler, buf, responseSender) -> {
                //$$     BlockPos pos = buf.readBlockPos();
                //$$     int color = buf.readInt();
                //$$     int duration = buf.readInt();
                //$$     boolean seeThrough = buf.readBoolean();
                //$$     boolean permanent = buf.readBoolean();
                //$$     client.execute(() -> HighlightBlocksRenderer.addHighlight(pos, color, duration, seeThrough, permanent));
                //$$ }
                //#else
                (payload, context) -> context.client().execute(() ->
                        HighlightBlocksRenderer.addHighlight(payload.pos(), payload.color(), payload.durationTicks(), payload.seeThrough(), payload.permanent())
                )
                //#endif
        );
        ClientPlayNetworking.registerGlobalReceiver(
                //#if MC < 12005
                //$$ IGNYServer.REMOVE_HIGHLIGHT_PACKET_ID,
                //#else
                RemoveHighlightPayload.TYPE,
                //#endif
                //#if MC < 12005
                //$$ (client, handler, buf, responseSender) -> {
                //$$     BlockPos pos = buf.readBlockPos();
                //$$     client.execute(() -> HighlightBlocksRenderer.removeHighlight(pos));
                //$$ }
                //#else
                (payload, context) -> context.client().execute(() ->
                        HighlightBlocksRenderer.removeHighlight(payload.pos())
                )
                //#endif
        );
    }
}
