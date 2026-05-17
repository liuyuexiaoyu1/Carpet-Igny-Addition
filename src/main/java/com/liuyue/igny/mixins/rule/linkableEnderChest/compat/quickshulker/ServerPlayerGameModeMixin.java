package com.liuyue.igny.mixins.rule.linkableEnderChest.compat.quickshulker;

import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
//#endif
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ServerPlayerGameMode.class, priority = 999)
@SuppressWarnings("all")
public class ServerPlayerGameModeMixin {
    @WrapMethod(method = "useItem")
    private InteractionResult useItem(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, Operation<InteractionResult> original) {
        if (LinkedContainerManager.isRuleEnabled()) {
            //#if MC >= 12005
            if (stack.is(Items.ENDER_CHEST) && stack.has(DataComponents.CUSTOM_NAME))
            //#else
            //$$ if (stack.is(Items.ENDER_CHEST) && stack.hasCustomHoverName())
            //#endif
            {
                try {
                    //#if MC >= 12005
                    String name = stack.get(DataComponents.CUSTOM_NAME).getString();
                    ((ViewingChest) player).igny$setLinkedKey(name);
                    //#else
                    //$$ ((ViewingChest) player).igny$setLinkedKey(stack.getHoverName().getString());
                    //#endif
                    return original.call(player, level, stack, hand);
                } finally {
                    ((ViewingChest) player).igny$setLinkedKey(null);
                }
            }
        }
        return original.call(player, level, stack, hand);
    }
}
