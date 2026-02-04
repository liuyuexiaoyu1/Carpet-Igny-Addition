package com.liuyue.igny.mixins.logger.beacon;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import com.liuyue.igny.mixins.logger.LoggerAccessor;
import com.liuyue.igny.network.packet.render.BoxPayload;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
//#if MC < 12005
//$$ import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import com.liuyue.igny.IGNYServer;
//#endif

import java.util.List;

@Mixin(value = BeaconBlockEntity.class, priority = 999)
public abstract class BeaconBlockEntityMixin {

    @Shadow
    private static int updateBase(Level level, int i, int j, int k) {
        return 0;
    }

    @WrapOperation(
            method = "applyEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;")
    )
    private static List<?> wrapApplyEffectsAABB(Level instance, Class<?> aClass, AABB aabb, Operation<List<?>> original, @Local(argsOnly = true) BlockPos pos) {
        Logger logger = LoggerRegistry.getLogger("beacon");
        if (logger.hasOnlineSubscribers() && !instance.isClientSide() && instance instanceof ServerLevel serverLevel) {
            //#if MC < 12005
            //$$ FriendlyByteBuf buf = PacketByteBufs.create();
            //$$ buf.writeBlockPos(pos);
            //$$ buf.writeInt(0x4400FFFF);
            //$$ buf.writeInt(90);
            //$$ buf.writeBoolean(false);
            //$$ buf.writeBoolean(true);
            //$$ buf.writeDouble(aabb.minX); buf.writeDouble(aabb.minY); buf.writeDouble(aabb.minZ);
            //$$ buf.writeDouble(aabb.maxX); buf.writeDouble(aabb.maxY); buf.writeDouble(aabb.maxZ);
            //$$ buf.writeBoolean(true); buf.writeBoolean(false); buf.writeBoolean(true);
            //#else
            BoxPayload payload = new BoxPayload(
                    pos, 0x4400FFFF, 90, false, true, aabb,
                    true, false, true
            );
            //#endif

            LevelChunk chunk = serverLevel.getChunkAt(pos);
            ServerChunkCache chunkSource = serverLevel.getChunkSource();
            chunkSource.chunkMap.getPlayers(chunk.getPos(), false)
                    .forEach(player -> {
                        String name = player.getGameProfile().
                                //#if MC >= 12110
                                //$$ name();
                                //#else
                                        getName();
                        //#endif
                        if (!((LoggerAccessor) logger).getSubscribedOnlinePlayers().containsKey(name)) return;

                        //#if MC >= 12005
                        if (ServerPlayNetworking.canSend(player, BoxPayload.TYPE)) ServerPlayNetworking.send(player, payload);
                        //#else
                        //$$ if (ServerPlayNetworking.canSend(player, IGNYServer.RENDER_BOX_PACKET_ID)) ServerPlayNetworking.send(player, IGNYServer.RENDER_BOX_PACKET_ID, buf);
                        //#endif
                    });
        }

        return original.call(instance, aClass, aabb);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 1)
    )
    private static boolean wrapTickCheckActive(List<?> list, Operation<Boolean> original, @Local(argsOnly = true) BeaconBlockEntity beacon) {
        BlockPos pos = beacon.getBlockPos();
        Level level = beacon.getLevel();
        Logger logger = LoggerRegistry.getLogger("beacon");

        if (logger.hasOnlineSubscribers() && level instanceof ServerLevel serverLevel && (list.isEmpty() || updateBase(level, pos.getX(), pos.getY(), pos.getZ()) == 0)) {

            //#if MC < 12005
            //$$ FriendlyByteBuf stopBuf = PacketByteBufs.create();
            //$$ stopBuf.writeBlockPos(pos);
            //$$ stopBuf.writeInt(0); stopBuf.writeInt(0);
            //$$ stopBuf.writeBoolean(false); stopBuf.writeBoolean(true);
            //$$ stopBuf.writeDouble(beacon.getBlockPos().getX()); stopBuf.writeDouble(beacon.getBlockPos().getY()); stopBuf.writeDouble(beacon.getBlockPos().getZ());
            //$$ stopBuf.writeDouble(beacon.getBlockPos().getX()); stopBuf.writeDouble(beacon.getBlockPos().getY()); stopBuf.writeDouble(beacon.getBlockPos().getZ());
            //$$ stopBuf.writeBoolean(true); stopBuf.writeBoolean(false); stopBuf.writeBoolean(true);
            //#else
            BoxPayload stopPayload = new BoxPayload(
                    beacon.getBlockPos(), 0, 0, false, true, new AABB(beacon.getBlockPos()),
                    true, false, true
            );
            //#endif

            for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
                String name = player.getGameProfile().
                        //#if MC >= 12110
                        //$$ name();
                        //#else
                                getName();
                //#endif
                if (!((LoggerAccessor) logger).getSubscribedOnlinePlayers().containsKey(name)) continue;

                //#if MC >= 12005
                if (ServerPlayNetworking.canSend(player, BoxPayload.TYPE)) ServerPlayNetworking.send(player, stopPayload);
                //#else
                //$$ if (ServerPlayNetworking.canSend(player, IGNYServer.RENDER_BOX_PACKET_ID)) ServerPlayNetworking.send(player, IGNYServer.RENDER_BOX_PACKET_ID, stopBuf);
                //#endif
            }
        }
        return original.call(list);
    }
}