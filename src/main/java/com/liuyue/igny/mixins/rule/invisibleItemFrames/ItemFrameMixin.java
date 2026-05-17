package com.liuyue.igny.mixins.rule.invisibleItemFrames;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrame.class)
public class ItemFrameMixin {
    @Inject(method = "setItem(Lnet/minecraft/world/item/ItemStack;Z)V", at = @At(value = "RETURN"))
    private void onSetItem(ItemStack stack, boolean updateNeighbours, CallbackInfo ci) {
        ItemFrame self = (ItemFrame) (Object) this;
        if (
                !"false".equalsIgnoreCase(IGNYSettings.invisibleItemFrames) &&
                self.hasCustomName() &&
                (("true".equalsIgnoreCase(IGNYSettings.invisibleItemFrames) &&
                                "invisible".equalsIgnoreCase(self.getName().getString())) ||
                        self.getName().getString().equalsIgnoreCase(IGNYSettings.invisibleItemFrames))
        ) {
            self.setInvisible(!stack.isEmpty());
        }
    }
}
