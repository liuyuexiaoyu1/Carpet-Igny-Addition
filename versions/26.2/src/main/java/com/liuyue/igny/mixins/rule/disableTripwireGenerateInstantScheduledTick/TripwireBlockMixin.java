package com.liuyue.igny.mixins.rule.disableTripwireGenerateInstantScheduledTick;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TripWireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TripWireBlock.class)
public class TripwireBlockMixin {
    @WrapOperation(method = "checkPressed(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"))
    private void scheduleTick(Level instance, BlockPos pos, Block block, int i, Operation<Void> original) {
        if (IGNYSettings.DISABLE_TRIPWIRE_GENERATE_INSTANT_SCHEDULED_TICK.value()) {
            original.call(instance, pos, block, 1);
            return;
        }
        original.call(instance, pos, block, i);
    }
}
