package com.liuyue.igny.mixins.rule.invisibleItemFrames;

import com.llamalad7.mixinextras.sugar.Local;
//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
//#endif
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HangingEntityItem.class)
public class HangingEntityItemMixin {
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local HangingEntity entity) {
        ItemStack stack = context.getItemInHand();

        //#if MC >= 12005
        if (entity instanceof ItemFrame && stack.has(DataComponents.CUSTOM_NAME))
        //#else
        //$$ if (entity instanceof ItemFrame && stack.hasCustomHoverName())
        //#endif
        {
            entity.setCustomName(stack.getHoverName());
        }
    }
}
