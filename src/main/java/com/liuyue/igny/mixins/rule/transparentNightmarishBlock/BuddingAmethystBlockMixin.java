package com.liuyue.igny.mixins.rule.transparentNightmarishBlock;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BuddingAmethystBlock.class)
public class BuddingAmethystBlockMixin {
    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean randomTick(ServerLevel instance, BlockPos pos, BlockState state, Operation<Boolean> original) {
        if (IGNYSettings.TRANSPARENT_NIGHTMARISH_BLOCK.value()) {
            return instance.setBlock(pos, state, 2 | 16);
        }
        return original.call(instance, pos, state);
    }
}
