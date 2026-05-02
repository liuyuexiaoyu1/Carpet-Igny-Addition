package com.liuyue.igny.mixins.rule.unblockableAmethyst;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BuddingAmethystBlock.class)
public class BuddingAmethystBlockMixin {
    @WrapOperation(method = "canClusterGrowAtState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;getAmount()I"))
    private static int getAmount(FluidState instance, Operation<Integer> original) {
        return IGNYSettings.unblockableAmethyst ? 8 : original.call(instance);
    }
}
