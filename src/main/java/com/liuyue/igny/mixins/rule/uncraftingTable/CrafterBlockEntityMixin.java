package com.liuyue.igny.mixins.rule.uncraftingTable;

import com.liuyue.igny.utils.uncraftingTable.UncraftingTable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrafterBlockEntity.class)
public abstract class CrafterBlockEntityMixin {

    @Inject(method = "canPlaceItem", at = @At(value = "HEAD"), cancellable = true)
    private void canPlaceItem(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!UncraftingTable.isUncraftMode(((CrafterBlockEntity) (Object) this).getCustomName())) {
            return;
        }

        cir.setReturnValue(slot == UncraftingTable.CRAFTER_RESULT_SLOT);
    }
}
