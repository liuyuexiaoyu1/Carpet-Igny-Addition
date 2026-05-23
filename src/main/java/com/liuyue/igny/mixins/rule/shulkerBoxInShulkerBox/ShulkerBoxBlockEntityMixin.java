package com.liuyue.igny.mixins.rule.shulkerBoxInShulkerBox;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin {
    @Inject(method = "canPlaceItemThroughFace", at = @At(value = "RETURN"), cancellable = true)
    private void canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.SHULKER_BOX_IN_SHULKER_BOX.value()) {
            cir.setReturnValue(true);
        }
    }
}
