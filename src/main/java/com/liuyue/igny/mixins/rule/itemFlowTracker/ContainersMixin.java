package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Containers.class)
public abstract class ContainersMixin {
    @Inject(
            method = "dropContents(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/Container;)V",
            at = @At(value = "HEAD")
    )
    private static void igny$markSpilledContents(Level level, BlockPos pos, Container container, CallbackInfo ci) {
        TrackMark blockMark = TrackingWatch.peekBlockMark(level, pos);

        if (blockMark == null) {
            return;
        }

        int total = 0;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            total += container.getItem(slot).getCount();
        }

        if (total > 0) {
            TrackMark spilled = Tracking.derive(blockMark, total);

            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack stack = container.getItem(slot);

                if (!stack.isEmpty()) {
                    Tracking.setIfAbsent(stack, spilled);
                }
            }
        }

        TrackingWatch.clearBlockMark(level, pos);
    }
}
