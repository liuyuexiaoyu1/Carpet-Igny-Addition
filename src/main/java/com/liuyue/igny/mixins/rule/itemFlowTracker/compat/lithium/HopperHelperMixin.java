package com.liuyue.igny.mixins.rule.itemFlowTracker.compat.lithium;

import com.liuyue.igny.utils.itemFlowTracker.compat.lithium.HopperTransfer;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Restriction(
        require = {
                @Condition(value = "lithium", versionPredicates = ">=0.13")
        }
)
@Mixin(targets = "net.caffeinemc.mods.lithium.common.hopper.HopperHelper")
public abstract class HopperHelperMixin {
    @WrapMethod(method = "tryMoveSingleItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Z")
    private static boolean igny$tryMoveSingleItem(Container target, ItemStack stack, @Nullable Direction direction, Operation<Boolean> original) {
        HopperTransfer.Move move = HopperTransfer.prepare(target, direction, stack);
        boolean moved = original.call(target, stack, direction);

        if (move != null) {
            HopperTransfer.apply(move, moved);
        }

        return moved;
    }
}
