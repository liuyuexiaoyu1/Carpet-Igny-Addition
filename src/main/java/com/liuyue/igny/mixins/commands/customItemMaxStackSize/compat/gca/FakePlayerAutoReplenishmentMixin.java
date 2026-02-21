package com.liuyue.igny.mixins.commands.customItemMaxStackSize.compat.gca;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.dubhe.gugle.carpet.tools.player.FakePlayerAutoReplenishment")
@Pseudo
public abstract class FakePlayerAutoReplenishmentMixin {
    @Inject(method = "pickItemFromBox", at = @At(value = "HEAD"), cancellable = true)
    private static void pickItemFromBox(ItemStack shulkerBox, ItemStack itemStack, int count, CallbackInfoReturnable<Integer> cir) {
        if (shulkerBox.getCount() > 1) cir.setReturnValue(0);
    }
}