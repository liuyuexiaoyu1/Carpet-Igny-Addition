package com.liuyue.igny.mixins.rule.easyPlaceAccurateRail;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {
    @WrapOperation(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;onPlace(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"))
    private void onPlace(BlockState instance, Level level, BlockPos pos, BlockState state, boolean b, Operation<Void> original) {
        if (IGNYSettings.EASY_PLACE_ACCURATE_RAIL.value() && IGNYSettings.easyPlaceProtocolActive.get() && state.getBlock() instanceof BaseRailBlock) {
            return;
        }
        original.call(instance, level, pos, state, b);
    }
}
