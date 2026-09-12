package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.utils.itemFlowTracker.core.Nesting;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    @Inject(
            method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z",
            at = @At(value = "RETURN")
    )
    private static void igny$pickedUpFrom(Container container, ItemEntity itemEntity, CallbackInfoReturnable<Boolean> cir) {
        TrackingWatch.onEnterContainer(container, itemEntity.position());
    }

    @Inject(
            method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "RETURN")
    )
    private static void igny$refundRejected(Container source, Container target, ItemStack stack, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack rejected = cir.getReturnValue();

        if (rejected != null && !rejected.isEmpty()) {
            Tracking.returned(rejected);
        }
    }

    @Inject(
            method = "tryMoveInItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER)
    )
    private static void igny$movedWholeStack(Container source, Container target, ItemStack stack, int slot, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        if (Tracking.getRaw(stack) != null) {
            Tracking.moved(stack);
        }

        if (Nesting.carriesMark(stack)) {
            TrackingWatch.onEnterContainer(target, TrackingWatch.positionOf(source));
        }
    }

    @Inject(
            method = "tryMoveInItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V")
    )
    private static void igny$mergedIntoSlot(Container source, Container target, ItemStack stack, int slot, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        TrackMark mark = Tracking.getRaw(stack);

        if (mark != null) {
            ItemStack destination = target.getItem(slot);
            int moved = Math.min(stack.getCount(), stack.getMaxStackSize() - destination.getCount());
            Tracking.arrive(destination, mark, moved);
        }

        if (Nesting.carriesMark(stack)) {
            TrackingWatch.onEnterContainer(target, TrackingWatch.positionOf(source));
        }
    }
}
