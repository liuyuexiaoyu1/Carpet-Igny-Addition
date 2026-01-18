package com.liuyue.igny.mixins.carpet.commands;

import carpet.helpers.EntityPlayerActionPack;
import com.liuyue.igny.mixins.carpet.accessors.EntityPlayerActionPackAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerActionPack.class)
public class EntityPlayerActionPackMixin {

    @Shadow
    @Final
    private ServerPlayer player;

    @Unique
    private void carpet$dropItemFromSlot(int slot, boolean dropAll) {
        Inventory inv = player.getInventory();
        if (!inv.getItem(slot).isEmpty()) {
            player.drop(inv.removeItem(slot, dropAll ? inv.getItem(slot).getCount() : 1), false, true);
        }
    }

    @Unique
    private void carpet$dropEnderChestItemFromSlot(int slot, boolean dropAll) {
        int enderChestSlot = slot - 200;
        ItemStack enderChestItem = player.getEnderChestInventory().getItem(enderChestSlot);

        if (!enderChestItem.isEmpty()) {
            ItemStack dropStack = enderChestItem.copy();
            if (!dropAll) {
                dropStack.setCount(1);
            }

            if (dropAll) {
                player.getEnderChestInventory().setItem(enderChestSlot, ItemStack.EMPTY);
            } else {
                enderChestItem.shrink(1);
                if (enderChestItem.isEmpty()) {
                    player.getEnderChestInventory().setItem(enderChestSlot, ItemStack.EMPTY);
                }
            }

            player.drop(dropStack, false, true);
        }
    }

    @Inject(method = "drop(IZ)V", at = @At("HEAD"), cancellable = true,remap = false)
    private void onDrop(int selectedSlot, boolean dropAll, CallbackInfo ci) {
        EntityPlayerActionPack instance = (EntityPlayerActionPack) (Object) this;
        Player player = ((EntityPlayerActionPackAccessor) instance).getPlayer();
        if (selectedSlot == -2 || selectedSlot == -3 || selectedSlot == -4) {
            Inventory inv = player.getInventory();
            if (selectedSlot == -2 || selectedSlot == -3) {
                for (int i = inv.getContainerSize() - 1; i >= 0; i--) {
                    carpet$dropItemFromSlot(i, dropAll);
                }
            }
            if (selectedSlot == -2 || selectedSlot == -4) {
                for (int i = 0; i < 27; i++) {
                    carpet$dropEnderChestItemFromSlot(200 + i, dropAll);
                }
            }

            ci.cancel();
        }
        else if (selectedSlot >= 200 && selectedSlot <= 226) {
            carpet$dropEnderChestItemFromSlot(selectedSlot, dropAll);
            ci.cancel();
        }
    }
}