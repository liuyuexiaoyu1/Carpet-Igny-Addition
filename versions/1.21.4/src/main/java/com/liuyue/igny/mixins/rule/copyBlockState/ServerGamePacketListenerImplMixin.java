package com.liuyue.igny.mixins.rule.copyBlockState;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundPickItemFromBlockPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Inject(method = "handlePickItemFromBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;addBlockDataToItem(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void addBlockDataToItem(ServerboundPickItemFromBlockPacket packet, CallbackInfo ci, @Local ItemStack itemStack) {
        if (IGNYSettings.COPY_BLOCK_STATE.value()) {
            ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
            Player player = handler.player;
            Level level = player.level();
            BlockPos blockPos = packet.pos();
            if (player.isShiftKeyDown()) {
                BlockState blockState = level.getBlockState(blockPos);
                setBlockStateData(itemStack, blockState);
            }
        }
    }

    @Unique
    private static void setBlockStateData(ItemStack stack, BlockState state) {
        Map<String, String> map = new HashMap<>();

        for (Property<?> property : state.getProperties()) {
            setPropertyToMap(state, (Property<?>) property, map);
        }

        BlockItemStateProperties component = new BlockItemStateProperties(map);
        stack.set(DataComponents.BLOCK_STATE, component);
    }

    @Unique
    private static <T extends Comparable<T>> void setPropertyToMap(BlockState state, Property<T> property, Map<String, String> map) {
        T value = state.getValue(property);
        map.put(property.getName(), property.getName(value));
    }
}
