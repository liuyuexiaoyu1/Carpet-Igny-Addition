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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//#if MC >= 12111
//$$ import fi.dy.masa.servux.util.data.tag.converter.DataConverterNbt;
//#endif

@Restriction(require = @Condition("servux"))
@Mixin(ServuxEntitiesHandler.class)
public class ServuxEntitiesHandlerMixin {

    @ModifyVariable(method = "encodeServerData", at = @At(value = "HEAD"), argsOnly = true, index = 2)
    private IServerPayloadData igny$injectLinkedChest(IServerPayloadData data, ServerPlayer requester) {
        if (!(data instanceof ServuxEntitiesPacket packet)) return data;
        Level level = requester.level();
        if (!(level instanceof ServerLevel serverLevel)) return data;
        ServuxEntitiesPacket.Type type = packet.getType();
        if (type == ServuxEntitiesPacket.Type.PACKET_S2C_BLOCK_NBT_RESPONSE_SIMPLE) {
            BlockPos pos = packet.getPos();
            if (pos == null) return data;
            BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
            if (!(blockEntity instanceof LinkedEnderChest linked)) return data;
            Container container = linked.igny$getContainer();
            if (!(container instanceof LinkedContainer linkedContainer)) return data;
            Tag items = linkedContainer.igny$createItemsTag(serverLevel.registryAccess());
            return items == null ? data : igny$writeItems(packet, "Items", items, data);
        }
        if (type == ServuxEntitiesPacket.Type.PACKET_S2C_ENTITY_NBT_RESPONSE_SIMPLE) {
            if (requester.getId() != packet.getEntityId()) return data;
            if (!(requester instanceof ViewingChest viewing)) return data;
            String key = viewing.igny$getLinkedKey();
            if (key == null) return data;
            Tag items = LinkedContainerManager.get(key).igny$createItemsTag(serverLevel.registryAccess());
            return items == null ? data : igny$writeItems(packet, "EnderItems", items, data);
        }
        return data;
    }

    @Unique
    private static IServerPayloadData igny$writeItems(ServuxEntitiesPacket packet, String key, Tag items, IServerPayloadData data) {
        //#if MC >= 12111
        //$$ fi.dy.masa.servux.util.data.tag.CompoundData compound = packet.getCompound();
        //$$ if (compound == null) return data;
        //$$ compound.put(key, DataConverterNbt.fromVanillaNbt(items));
        //#else
        CompoundTag compound = packet.getCompound();
        if (compound == null) return data;
        compound.put(key, items);
        //#endif
        return data;
    }
}
