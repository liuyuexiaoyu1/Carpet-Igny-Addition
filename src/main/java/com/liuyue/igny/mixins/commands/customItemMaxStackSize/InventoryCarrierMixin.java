package com.liuyue.igny.mixins.commands.customItemMaxStackSize;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.manager.CustomItemMaxStackSizeDataManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryCarrier.class)
public interface InventoryCarrierMixin {
    @WrapOperation(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SimpleContainer;addItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack addItem(SimpleContainer instance, ItemStack stack, Operation<ItemStack> original) {
        int customMax = CustomItemMaxStackSizeDataManager.INSTANCE.getCustomStackSize(stack);
        if (IGNYSettings.itemStackCountChanged.get() && customMax != -1) {
            int defaultMaxSize = stack.getItem().getDefaultMaxStackSize();
            if (stack.getCount() > defaultMaxSize) {
                ItemStack itemStack = original.call(instance, stack.copyWithCount(defaultMaxSize));
                stack.setCount(stack.getCount() - defaultMaxSize + itemStack.getCount());
                return stack;
            }
        }
        return original.call(instance, stack);
    }
}
