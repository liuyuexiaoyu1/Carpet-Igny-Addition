package com.liuyue.igny.mixins.commands.customItemMaxStackSize.compat.lithium;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "me.jellysquid.mods.lithium.common.hopper.LithiumStackList")
@Pseudo
public abstract class OldVersionLithiumStackListMixin {
    //#if MC >= 12006
    @WrapOperation(method = "<init>(Lnet/minecraft/core/NonNullList;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I"))
    private int getMaxStackSize(ItemStack instance, Operation<Integer> original) {
        boolean changed = IGNYSettings.itemStackCountChanged.get();
        try {
            IGNYSettings.itemStackCountChanged.set(false);
            return original.call(instance);
        } finally {
            IGNYSettings.itemStackCountChanged.set(changed);
        }
    }

    @WrapOperation(method = "lithium$notifyBeforeCountChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I"))
    private int getMaxStackSizeNotify(ItemStack instance, Operation<Integer> original) {
        boolean changed = IGNYSettings.itemStackCountChanged.get();
        try {
            IGNYSettings.itemStackCountChanged.set(false);
            return original.call(instance);
        } finally {
            IGNYSettings.itemStackCountChanged.set(changed);
        }
    }
    //#endif
}