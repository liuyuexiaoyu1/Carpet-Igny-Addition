package com.liuyue.igny;

import com.liuyue.igny.mixins.rule.instantPortalTeleport.EntityInvoker;
import com.liuyue.igny.network.packet.config.SyncLinkedEnderChestPayload;
import com.liuyue.igny.utils.EntityUtil;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
//#if MC >= 12101
import net.minecraft.world.level.block.Portal;
//#else
//$$ import net.minecraft.world.level.block.Blocks;
//$$ import net.minecraft.world.level.block.state.BlockState;
//#endif
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
                //$$ (server, player, impl, buf, sender) -> {
                //#else
                (payload, context) -> {
                    //#endif
                    //#if MC < 12005
                    //$$ String chestName = buf.readUtf();
                    //#else
                    String chestName = payload.key();
                    Player player = context.player();
                    //#endif
                    //#if MC < 12005
                    //$$ server.execute(() -> {
                    //#else
                    context.server().execute(() -> {
                        //#endif
                        if (chestName == null || chestName.isEmpty()) {
                            ((ViewingChest) player).igny$setLinkedKey(null);
                        } else {
                            ((ViewingChest) player).igny$setLinkedKey(chestName);
                        }
                    });
                }
        );
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverLevel) -> {
            if (!IGNYSettings.INSTANT_PORTAL_TELEPORT.value()) return;
            if (entity.isOnPortalCooldown()) return;

            ServerLevel level = (ServerLevel) entity.level();
            BlockPos portalPos = EntityUtil.findPortalInBoundingBox(level, entity.getBoundingBox());
            if (portalPos == null) return;
            //#if MC >= 12101
            if (level.getBlockState(portalPos).getBlock() instanceof Portal portal && entity.canUsePortal(false)) {
                entity.setAsInsidePortal(portal, portalPos);
                ((EntityInvoker) entity).invokeHandlePortal();
            }
            //#else
            //$$ BlockState state = level.getBlockState(portalPos);
            //$$ if (!state.is(Blocks.NETHER_PORTAL) && !state.is(Blocks.END_PORTAL)) return;
            //$$ EntityInvoker invoker = (EntityInvoker) entity;
            //$$ if (entity.canChangeDimensions()) {
            //$$     entity.handleInsidePortal(portalPos);
            //$$     if (state.is(Blocks.NETHER_PORTAL)) {
            //$$         invoker.igny$invokeHandleNetherPortal();
            //$$     }
            //$$ }
            //#endif
        });
    }
}
