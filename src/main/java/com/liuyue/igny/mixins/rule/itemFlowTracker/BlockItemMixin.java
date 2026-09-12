package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.utils.itemFlowTracker.core.Nesting;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(
            method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "RETURN")
    )
    private void igny$carryMarkIntoBlock(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = cir.getReturnValue();

        if (result == null || !result.consumesAction() || !(context.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ItemStack placed = context.getItemInHand();
        BlockPos pos = context.getClickedPos();

        if (!(level.getBlockEntity(pos) instanceof Container)) {
            return;
        }

        TrackMark mark = Tracking.getRaw(placed);

        if (mark != null) {
            TrackingWatch.markBlock(level, pos, mark);
        } else if (Nesting.carriesMark(placed)) {
            TrackingWatch.watchBlock(level, pos);
        }
    }
}
