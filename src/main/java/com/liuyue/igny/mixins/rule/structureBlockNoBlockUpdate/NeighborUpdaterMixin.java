package com.liuyue.igny.mixins.rule.structureBlockNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NeighborUpdater.class)
public interface NeighborUpdaterMixin {
    @Inject(method = "executeShapeUpdate", at = @At("HEAD"), cancellable = true)
    private static void executeShapeUpdate(LevelAccessor levelAccessor, Direction direction, BlockState blockState, BlockPos blockPos, BlockPos blockPos2, int i, int j, CallbackInfo ci) {
        if (IGNYSettings.structureBlockNoBlockUpdate && (IGNYSettings.noUpdatePos.contains(blockPos)|| IGNYSettings.noUpdatePos.contains(blockPos2))) ci.cancel();
    }

    @Inject(method = "executeUpdate", at = @At("HEAD"), cancellable = true)
    private static void executeUpdate(Level level, BlockState blockState, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl, CallbackInfo ci) {
        if (IGNYSettings.structureBlockNoBlockUpdate && (IGNYSettings.noUpdatePos.contains(blockPos) || IGNYSettings.noUpdatePos.contains(blockPos2))) ci.cancel();
    }
}
