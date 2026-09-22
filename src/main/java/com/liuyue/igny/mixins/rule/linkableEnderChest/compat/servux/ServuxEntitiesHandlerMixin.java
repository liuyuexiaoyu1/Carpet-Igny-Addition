package com.liuyue.igny.mixins.rule.linkableEnderChest.compat.servux;

import com.liuyue.igny.helper.inventory.LinkedContainer;
import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.LinkedEnderChest;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
import fi.dy.masa.servux.network.IServerPayloadData;
import fi.dy.masa.servux.network.packet.ServuxEntitiesHandler;
import fi.dy.masa.servux.network.packet.ServuxEntitiesPacket;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 12111
//$$ import fi.dy.masa.servux.util.data.tag.converter.DataConverterNbt;
//#endif

@Restriction(require = @Condition("servux"))
@Mixin(ServuxEntitiesHandler.class)
public abstract class ServuxEntitiesHandlerMixin {

    //#if MC >= 12111
    //$$ @Inject(method = "encodeServerData", at = @At("HEAD"), cancellable = true)
    //$$ private void igny$injectLinkedChest(ServerPlayer requester, IServerPayloadData data, CallbackInfo ci) {
    //$$     if (!(data instanceof ServuxEntitiesPacket packet)) {
    //$$         return;
    //$$     }
    //$$
    //$$     Level level = requester.level();
    //$$
    //$$     if (!(level instanceof ServerLevel serverLevel)) {
    //$$         return;
    //$$     }
    //$$
    //$$     ServuxEntitiesPacket.Type type = packet.getType();
    //$$     BlockPos pos;
    //$$     Tag items;
    //$$
    //$$     if (type == ServuxEntitiesPacket.Type.PACKET_S2C_BLOCK_NBT_RESPONSE_SIMPLE) {
    //$$         pos = packet.getPos();
    //$$
    //$$         if (pos == null) {
    //$$             return;
    //$$         }
    //$$
    //$$         BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
    //$$
    //$$         if (!(blockEntity instanceof LinkedEnderChest linked)) {
    //$$             return;
    //$$         }
    //$$
    //$$         Container container = linked.igny$getContainer();
    //$$
    //$$         if (!(container instanceof LinkedContainer linkedContainer)) {
    //$$             return;
    //$$         }
    //$$
    //$$         items = linkedContainer.igny$createItemsTag(serverLevel.registryAccess());
    //$$
    //$$         if (items == null) {
    //$$             return;
    //$$         }
    //$$
    //$$         igny$write(packet, "Items", items, ci);
    //$$         return;
    //$$     }
    //$$
    //$$     if (type == ServuxEntitiesPacket.Type.PACKET_S2C_ENTITY_NBT_RESPONSE_SIMPLE) {
    //$$         if (requester.getId() != packet.getEntityId() || !(requester instanceof ViewingChest viewing)) {
    //$$             return;
    //$$         }
    //$$
    //$$         String key = viewing.igny$getLinkedKey();
    //$$
    //$$         if (key == null) {
    //$$             return;
    //$$         }
    //$$
    //$$         items = LinkedContainerManager.get(key).igny$createItemsTag(serverLevel.registryAccess());
    //$$
    //$$         if (items == null) {
    //$$             return;
    //$$         }
    //$$
    //$$         igny$write(packet, "EnderItems", items, ci);
    //$$     }
    //$$ }
    //#else
    @Inject(method = "encodeServerData", at = @At("HEAD"), cancellable = true)
    private void igny$injectLinkedChest(ServerPlayer requester, IServerPayloadData data, CallbackInfo ci) {
        if (!(data instanceof ServuxEntitiesPacket packet)) {
            return;
        }

        Level level = requester.level();

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ServuxEntitiesPacket.Type type = packet.getType();
        BlockPos pos;
        Tag items;

        if (type == ServuxEntitiesPacket.Type.PACKET_S2C_BLOCK_NBT_RESPONSE_SIMPLE) {
            pos = packet.getPos();

            if (pos == null) {
                return;
            }

            BlockEntity blockEntity = serverLevel.getBlockEntity(pos);

            if (!(blockEntity instanceof LinkedEnderChest linked)) {
                return;
            }

            Container container = linked.igny$getContainer();

            if (!(container instanceof LinkedContainer linkedContainer)) {
                return;
            }

            items = linkedContainer.igny$createItemsTag(serverLevel.registryAccess());

            if (items == null) {
                return;
            }

            this.igny$write(packet, "Items", items, ci);
            return;
        }

        if (type == ServuxEntitiesPacket.Type.PACKET_S2C_ENTITY_NBT_RESPONSE_SIMPLE) {
            if (requester.getId() != packet.getEntityId() || !(requester instanceof ViewingChest viewing)) {
                return;
            }

            String key = viewing.igny$getLinkedKey();

            if (key == null) {
                return;
            }

            items = LinkedContainerManager.get(key).igny$createItemsTag(serverLevel.registryAccess());

            if (items == null) {
                return;
            }

            this.igny$write(packet, "EnderItems", items, ci);
        }
    }
    //#endif

    //#if MC >= 12111
    //$$ @Unique
    //$$ private void igny$write(ServuxEntitiesPacket packet, String key, Tag items, CallbackInfo ci) {
    //$$     fi.dy.masa.servux.util.data.tag.CompoundData compound = packet.getCompound();
    //$$
    //$$     if (compound == null) {
    //$$         return;
    //$$     }
    //$$
    //$$     compound.put(key, DataConverterNbt.fromVanillaNbt(items));
    //$$     ci.cancel();
    //$$ }
    //#else
    @Unique
    private void igny$write(ServuxEntitiesPacket packet, String key, Tag items, CallbackInfo ci) {
        CompoundTag compound = packet.getCompound();

        if (compound == null) {
            return;
        }

        compound.put(key, items);
        ci.cancel();
    }
    //#endif
}
