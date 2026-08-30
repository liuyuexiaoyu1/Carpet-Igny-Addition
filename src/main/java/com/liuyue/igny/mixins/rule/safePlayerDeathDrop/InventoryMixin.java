package com.liuyue.igny.mixins.rule.safePlayerDeathDrop;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Inventory.class)
public class InventoryMixin {
    //#if MC >= 26.3
    //$$ @WrapOperation(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;createItemStackToDrop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"))
    //#else
    @WrapOperation(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"))
    //#endif
    private ItemEntity drop(Player instance, ItemStack itemStack, boolean a, boolean b, Operation<ItemEntity> original) {
        if (IGNYSettings.SAFE_PLAYER_DEATH_DROP.value()) {
            ItemEntity itemEntity = original.call(instance, itemStack, a, b);
            if (itemEntity != null) {
                itemEntity.setDeltaMovement(Vec3.ZERO);
            }
            return itemEntity;
        }
        return original.call(instance, itemStack, a, b);
    }
}
