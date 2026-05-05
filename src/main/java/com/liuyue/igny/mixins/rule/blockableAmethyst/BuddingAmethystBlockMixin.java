package com.liuyue.igny.mixins.rule.blockableAmethyst;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BuddingAmethystBlock.class)
public class BuddingAmethystBlockMixin {
    //#if MC >= 26.1
    //$$ @WrapOperation(method = "canClusterGrowAtState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    //$$ private static boolean is(BlockState instance, Object block, Operation<Boolean> original)
    //#else
    @WrapOperation(method = "canClusterGrowAtState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean is(BlockState instance, Block block, Operation<Boolean> original)
    //#endif
    {
        return !IGNYSettings.blockableAmethyst && original.call(instance, block);
    }
}
