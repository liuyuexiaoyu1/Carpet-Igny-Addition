package com.liuyue.igny.mixins.commands.customPlayerPickupItem;

import com.liuyue.igny.data.CustomPickupDataManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    public void playerTouch(Player player, CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!self.level().isClientSide()){
            ItemStack stack = self.getItem();
            if (stack.isEmpty()) return;
            if (!CustomPickupDataManager.canPickUp(player.getName().getString(), BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) {
                ci.cancel();
            }
        }
    }
}
