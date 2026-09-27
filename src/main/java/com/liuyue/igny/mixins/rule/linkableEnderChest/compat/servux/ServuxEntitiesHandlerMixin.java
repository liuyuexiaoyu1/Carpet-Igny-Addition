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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? >= 1.21.11 ? import fi.dy.masa.servux.util.data.tag.converter.DataConverterNbt;

@Restriction(require = @Condition("servux"))
@Mixin(ServuxEntitiesHandler.class)
public abstract class ServuxEntitiesHandlerMixin {

    @Inject(method = "encodeServerData", at = @At("HEAD"), cancellable = true)
    private void igny$injectLinkedChest(ServerPlayer requester, IServerPayloadData payload, CallbackInfo ci) {
        if (!(payload instanceof ServuxEntitiesPacket packet) || !(requester.level() instanceof ServerLevel level)) {
            return;
        }

        ServuxEntitiesPacket.Type type = packet.getType();

        if (type == ServuxEntitiesPacket.Type.PACKET_S2C_BLOCK_NBT_RESPONSE_SIMPLE) {
            this.igny$write(packet, "Items", this.igny$blockContainer(level, packet.getPos()), level, ci);
            return;
        }

        if (type == ServuxEntitiesPacket.Type.PACKET_S2C_ENTITY_NBT_RESPONSE_SIMPLE
                && requester.getId() == packet.getEntityId()) {
            this.igny$write(packet, "EnderItems", this.igny$viewedContainer(requester), level, ci);
        }
    }

    @Unique
    @Nullable
    private Container igny$blockContainer(ServerLevel level, @Nullable BlockPos pos) {
        if (pos == null || !(level.getBlockEntity(pos) instanceof LinkedEnderChest linked)) {
            return null;
        }

        return linked.igny$getContainer();
    }

    @Unique
    @Nullable
    private Container igny$viewedContainer(ServerPlayer requester) {
        if (!(requester instanceof ViewingChest viewing)) {
            return null;
        }

        String key = viewing.igny$getLinkedKey();

        return key == null ? null : LinkedContainerManager.get(key);
    }

    @Unique
    private void igny$write(ServuxEntitiesPacket packet, String key, @Nullable Container container, ServerLevel level, CallbackInfo ci) {
        if (!(container instanceof LinkedContainer linked)) {
            return;
        }

        Tag items = linked.igny$createItemsTag(level.registryAccess());

        if (items == null) {
            return;
        }

        //#if >= 1.21.11
        /*$$fi.dy.masa.servux.util.data.tag.CompoundData compound = packet.getCompound();

        if (compound == null) {
            return;
        }

        compound.put(key, DataConverterNbt.fromVanillaNbt(items));$$*/
        //#else
        CompoundTag compound = packet.getCompound();

        if (compound == null) {
            return;
        }

        compound.put(key, items);
        //#endif

        ci.cancel();
    }
}
