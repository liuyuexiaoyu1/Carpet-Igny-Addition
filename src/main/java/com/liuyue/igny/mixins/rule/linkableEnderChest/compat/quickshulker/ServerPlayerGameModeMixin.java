package com.liuyue.igny.mixins.rule.linkableEnderChest.compat.quickshulker;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
//#endif
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayerGameMode.class, priority = 999)
public class ServerPlayerGameModeMixin {
    @Inject(method = "useItem", at = @At("HEAD"))
    private void useItem(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!IGNYSettings.linkableEnderChest) return;
        //#if MC >= 12005
        if (stack.is(Items.ENDER_CHEST) && stack.has(DataComponents.CUSTOM_NAME))
        //#else
        //$$ if (stack.is(Items.ENDER_CHEST) && stack.hasCustomHoverName())
        //#endif
        {
            //#if MC >= 12005
            String name = stack.get(DataComponents.CUSTOM_NAME).getString();
            ((ViewingChest) player).igny$setLinkedKey(name);
            //#else
            //$$ ((ViewingChest) player).igny$setLinkedKey(stack.getHoverName().getString());
            //#endif
        }
    }
}
