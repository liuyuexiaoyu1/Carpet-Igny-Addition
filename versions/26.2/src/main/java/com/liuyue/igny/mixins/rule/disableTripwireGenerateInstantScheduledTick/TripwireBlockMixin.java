package com.liuyue.igny.mixins.rule.disableTripwireGenerateInstantScheduledTick;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.block.TripWireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TripWireBlock.class)
public class TripwireBlockMixin {
    @ModifyArg(method = "checkPressed(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V", ordinal = 1))
    private int scheduleTick(int tickDelay) {
        return IGNYSettings.DISABLE_TRIPWIRE_GENERATE_INSTANT_SCHEDULED_TICK.value() ? 1 : tickDelay;
    }
}
