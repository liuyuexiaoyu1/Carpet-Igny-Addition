package com.liuyue.igny.mixins.rule.safePlayerDeathDrop;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Inventory.class)
public class InventoryMixin {
    @WrapOperation(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity drop(Player instance, ItemStack itemStack, boolean a, boolean b, Operation<ItemEntity> original) {
        if (IGNYSettings.safePlayerDeathDrop) {
            if (itemStack.isEmpty()) {
                return null;
            } else if (instance.level().isClientSide()) {
                instance.swing(InteractionHand.MAIN_HAND);
                return null;
            } else {
                ItemEntity item = new ItemEntity(instance.level(), instance.getX(), instance.getEyeY() - 0.3F, instance.getZ(), itemStack, 0 , 0 ,0);
                item.setPickUpDelay(40);
                instance.level().addFreshEntity(item);
                return item;
            }
        }
        return original.call(instance, itemStack, a, b);
    }
}
