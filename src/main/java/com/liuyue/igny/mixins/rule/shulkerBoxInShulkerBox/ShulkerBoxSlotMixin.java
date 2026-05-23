package com.liuyue.igny.mixins.rule.shulkerBoxInShulkerBox;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxSlot.class)
public class ShulkerBoxSlotMixin {
    @Inject(method = "mayPlace", at = @At(value = "RETURN"), cancellable = true)
    private void mayPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.SHULKER_BOX_IN_SHULKER_BOX.value()) {
            cir.setReturnValue(true);
        }
    }
}
