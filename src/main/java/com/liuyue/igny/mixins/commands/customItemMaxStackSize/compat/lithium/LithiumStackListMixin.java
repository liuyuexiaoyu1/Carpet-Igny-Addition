package com.liuyue.igny.mixins.commands.customItemMaxStackSize.compat.lithium;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(value = "lithium", versionPredicates = ">0.13.1"))
@Mixin(targets = "net.caffeinemc.mods.lithium.common.hopper.LithiumStackList")
public abstract class LithiumStackListMixin {
    //#if >= 1.20.6
    @WrapOperation(method = {
            "<init>(Lnet/minecraft/core/NonNullList;I)V",
            "changedALot",
            "lithium$notifyCount(Lnet/minecraft/world/item/ItemStack;II)V",
            "set(ILnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"
    }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I"))
    private int getMaxStackSize(ItemStack instance, Operation<Integer> original) {
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