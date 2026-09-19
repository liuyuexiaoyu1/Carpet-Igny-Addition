package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.utils.itemFlowTracker.core.Nesting;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
        if (cir.getReturnValueZ()) {
            ItemStack picked = itemEntity.getItem();
            TrackingWatch.onEnterContainer(container, itemEntity.position(), TrackingWatch.trailOf(itemEntity), Nesting.inStack(picked));
        }
    }

    @WrapMethod(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;")
    private static ItemStack igny$addItem(Container source, Container target, ItemStack stack, Direction direction, Operation<ItemStack> original) {
        TrackMark carried = Nesting.inStack(stack);
        TrackMark own = Tracking.get(stack);
        Item item = stack.getItem();
        ItemStack result = original.call(source, target, stack, direction);

        if (result != null && !result.isEmpty()) {
            Tracking.returned(result);
        }

        if (own != null) {
            for (int slot = 0; slot < target.getContainerSize(); slot++) {
                ItemStack now = target.getItem(slot);

                if (!now.isEmpty() && now.getItem() == item) {
                    Tracking.setIfAbsent(now, own);
                    break;
                }
            }
        }

        if (carried != null) {
            TrackingWatch.onEnterContainer(target, null, null, carried);
        }

        return result;
    }

    @Inject(
            method = "tryMoveInItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER)
    )
    private static void igny$movedWholeStack(Container source, Container target, ItemStack stack, int slot, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        if (Tracking.getRaw(stack) != null) {
            Tracking.moved(stack);
        }

        TrackMark carried = Nesting.inStack(stack);

        if (carried == null) {
            carried = TrackingWatch.blockMarkOf(source);

            if (carried != null) {
                Tracking.setIfAbsent(target.getItem(slot), carried);
            }
        }

        if (carried != null) {
            igny$movedInto(target, source, carried);
        }
    }

    @Unique
    private static void igny$movedInto(Container target, Container source, TrackMark carried) {
        Container leaf = TrackingWatch.holderOf(source, carried);
        TrackingWatch.onEnterContainer(target, TrackingWatch.positionOf(leaf), TrackingWatch.trailOf(leaf), carried);
        TrackingWatch.handOver(leaf);
    }

    @Inject(
            method = "tryMoveInItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V")
    )
    private static void igny$mergedIntoSlot(Container source, Container target, ItemStack stack, int slot, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack destination = target.getItem(slot);
        int moved = Math.min(stack.getCount(), stack.getMaxStackSize() - destination.getCount());

        if (Tracking.getRaw(stack) != null) {
            Tracking.arrive(destination, stack, moved);
        }

        Tracking.withdrawn(stack, moved);

        TrackMark carried = Nesting.inStack(stack);

        if (carried == null) {
            carried = TrackingWatch.blockMarkOf(source);

            if (carried != null) {
                Tracking.setIfAbsent(destination, carried);
            }
        }

        if (carried != null) {
            igny$movedInto(target, source, carried);
        }
    }
}
