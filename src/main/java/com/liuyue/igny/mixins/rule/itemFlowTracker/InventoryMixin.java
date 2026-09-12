package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Inject(
            method = "addResource(ILnet/minecraft/world/item/ItemStack;)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;grow(I)V")
    )
    private void igny$carryMarkIntoSlot(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!Tracking.isMarked(stack)) {
            return;
        }

        Inventory self = (Inventory) (Object) this;
        ItemStack destination = self.getItem(slot);
        int moved = Math.min(stack.getCount(), destination.getMaxStackSize() - destination.getCount());
        Tracking.arrive(destination, stack, moved);
    }
}
