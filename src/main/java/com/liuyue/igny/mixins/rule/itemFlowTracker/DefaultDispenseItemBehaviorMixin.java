package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DefaultDispenseItemBehavior.class)
public abstract class DefaultDispenseItemBehaviorMixin {
    @WrapMethod(method = "spawnItem")
    private static void spawnItem(Level level, ItemStack stack, int speed, Direction facing, Position position, Operation<Void> original) {
        try {
            TrackingWatch.beginEjection(BlockPos.containing(position.x(), position.y(), position.z()).relative(facing.getOpposite()));
            original.call(level, stack, speed, facing, position);
        } finally {
            TrackingWatch.endEjection();
        }
    }
}
