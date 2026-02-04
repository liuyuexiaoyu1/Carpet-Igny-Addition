package com.liuyue.igny.client;

import com.liuyue.igny.client.command.IGNYCommand;
import com.liuyue.igny.client.renderer.BaseTickingShapeRenderer;
import com.liuyue.igny.client.renderer.world.BoxRenderer;
import com.liuyue.igny.client.renderer.world.HighlightBlocksRenderer;
import com.liuyue.igny.data.CustomItemMaxStackSizeDataManager;
import com.liuyue.igny.network.packet.block.HighlightPayload;
import com.liuyue.igny.network.packet.block.RemoveHighlightPayload;
import com.liuyue.igny.network.packet.config.SyncCustomStackSizePayload;
import com.liuyue.igny.network.packet.render.BoxPayload;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
//#if MC < 12005
//$$ import com.liuyue.igny.IGNYServer;
//$$ import net.minecraft.core.BlockPos;
//$$ import java.util.Map;
//#else
//$$ import com.liuyue.igny.network.packet.block.HighlightPayload;
//#endif

public class IGNYClientRegister {
    public static void register() {
        registerNetworkPackReceiver();
        registerTickEvent();
        registerClientCommand();
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
                //$$     boolean permanent = buf.readBoolean();
                //$$     client.execute(() -> HighlightBlocksRenderer.addHighlight(pos, color, duration, permanent));
                //$$ }
                //#else
                (payload, context) -> context.client().execute(() ->
                        HighlightBlocksRenderer.addHighlight(payload.pos(), payload.color(), payload.durationTicks(), payload.permanent())
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
                //$$     client.execute(() -> HighlightBlocksRenderer.INSTANCE.remove(pos, null));
                //$$ }
                //#else
                (payload, context) -> context.client().execute(() ->
                        HighlightBlocksRenderer.INSTANCE.remove(payload.pos(), null)
                )
                //#endif
        );
        //#if MC >= 12006
        ClientPlayNetworking.registerGlobalReceiver(
                //#if MC < 12005
                //$$ IGNYServer.SYNC_STACK_SIZE_PACKET_ID,
                //#else
                SyncCustomStackSizePayload.TYPE,
                //#endif
                //#if MC < 12005
                //$$ (client, handler, buf, responseSender) -> {
                //$$     int size = buf.readVarInt();
                //$$     Map<String, Integer> map = new java.util.HashMap<>(size);
                //$$     for (int i = 0; i < size; i++) {
                //$$         map.put(buf.readUtf(), buf.readVarInt());
                //$$     }
                //$$     client.execute(() -> CustomItemMaxStackSizeDataManager.clientUpdateData(map));
                //$$ }
                //#else
                (payload, context) -> context.client().execute(() ->
                            CustomItemMaxStackSizeDataManager.clientUpdateData(payload.customStacks()

                //#endif
        )));
        //#endif
        ClientPlayNetworking.registerGlobalReceiver(
                //#if MC < 12005
                //$$ IGNYServer.RENDER_BOX_PACKET_ID,
                //#else
                BoxPayload.TYPE,
                //#endif
                //#if MC < 12005
                //$$ (client, handler, buf, responseSender) -> {
                //$$     BlockPos pos = buf.readBlockPos();
                //$$     int color = buf.readInt();
                //$$     int duration = buf.readInt();
                //$$     boolean permanent = buf.readBoolean();
                //$$     boolean depthTest = buf.readBoolean();
                //$$     double minX = buf.readDouble();
                //$$     double minY = buf.readDouble();
                //$$     double minZ = buf.readDouble();
                //$$     double maxX = buf.readDouble();
                //$$     double maxY = buf.readDouble();
                //$$     double maxZ = buf.readDouble();
                //$$     boolean withLine = buf.readBoolean();
                //$$     boolean lineDepthTest = buf.readBoolean();
                //$$     boolean smooth = buf.readBoolean();
                //$$     client.execute(() -> BoxRenderer.addBox(pos, color, duration, permanent, depthTest, minX, minY, minZ, maxX, maxY, maxZ, withLine, lineDepthTest, smooth));
                //$$ }
                //#else
                (payload, context) -> {
                    BlockPos pos = payload.pos();
                    int color = payload.color();
                    int duration = payload.durationTicks();
                    boolean permanent = payload.permanent();
                    boolean depthTest = payload.depthTest();
                    AABB box = payload.box();
                    double minX = box.minX;
                    double minY = box.minY;
                    double minZ = box.minZ;
                    double maxX = box.maxX;
                    double maxY = box.maxY;
                    double maxZ = box.maxZ;
                    boolean withLine = payload.withLine();
                    boolean lineDepthTest = payload.lineDepthTest();
                    boolean smooth = payload.smooth();
                    context.client().execute(() ->
                            BoxRenderer.addBox(pos, color, duration, permanent, depthTest, minX, minY, minZ, maxX, maxY, maxZ, withLine, lineDepthTest, smooth)
                    );
                }
                //#endif
        );
    }

    private static void registerTickEvent() {
        ClientTickEvents.
                //#if MC >= 26.1
                //$$ END_LEVEL_TICK
                //#else
                        END_WORLD_TICK
                //#endif
                .register(level -> {
            if (level != null) {
                BaseTickingShapeRenderer.tickAll();
            }
        });
    }

    private static void registerClientCommand(){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, r) -> {
            IGNYCommand.register(dispatcher);
        });
    }
}
