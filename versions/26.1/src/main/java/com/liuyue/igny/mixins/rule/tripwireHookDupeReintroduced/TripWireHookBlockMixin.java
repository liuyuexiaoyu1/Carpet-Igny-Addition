package com.liuyue.igny.mixins.rule.tripwireHookDupeReintroduced;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.block.TripWireHookBlock;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TripWireHookBlock.class)
public class TripWireHookBlockMixin {
    @WrapOperation(method = "calculateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z", ordinal = 2))
    private static boolean is(BlockState instance, Object o, Operation<Boolean> original) {
        return IGNYSettings.tripwireHookDupeReintroduced || original.call(instance, o);
    }
}