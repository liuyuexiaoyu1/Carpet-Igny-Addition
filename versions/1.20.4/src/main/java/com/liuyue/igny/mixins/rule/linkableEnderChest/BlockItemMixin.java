package com.liuyue.igny.mixins.rule.linkableEnderChest;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "updateCustomBlockEntityTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At(value = "TAIL"))
    private static void updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!(level.getBlockEntity(pos) instanceof EnderChestBlockEntity be)) {
            return;
        }
        if (stack.is(Items.ENDER_CHEST) && stack.hasCustomHoverName()) {
            CompoundTag tag = be.saveWithoutMetadata();
            tag.putString("CustomName", Component.Serializer.toJson(stack.getHoverName()));
            be.load(tag);
            be.setChanged();
        }
    }
}
