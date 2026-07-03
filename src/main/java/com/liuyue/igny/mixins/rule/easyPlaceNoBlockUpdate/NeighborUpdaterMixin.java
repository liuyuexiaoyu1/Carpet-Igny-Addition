package com.liuyue.igny.mixins.rule.easyPlaceNoBlockUpdate;

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

/**
 * 仅在 easyPlaceNoBlockUpdate=true 时生效：
 * 阻止协议放置期间的邻居更新和形状更新。
 */
@Mixin(NeighborUpdater.class)
public interface NeighborUpdaterMixin {
    @Inject(method = "executeShapeUpdate", at = @At("HEAD"), cancellable = true)
    private static void executeShapeUpdate(LevelAccessor levelAccessor, Direction direction, BlockState blockState, BlockPos blockPos, BlockPos blockPos2, int i, int j, CallbackInfo ci) {
        if ("true".equals(IGNYSettings.EASY_PLACE_NO_BLOCK_UPDATE.value()) && IGNYSettings.easyPlaceProtocolActive.get()) {
            ci.cancel();
        }
    }

    @SuppressWarnings("all")
    @Inject(method = "executeUpdate", at = @At("HEAD"), cancellable = true)
    //#if MC >= 12102
    //$$ private static void executeUpdate(Level level, BlockState blockState, BlockPos blockPos, Block block, net.minecraft.world.level.redstone.Orientation orientation, boolean bl, CallbackInfo ci) {
    //#else
    private static void executeUpdate(Level level, BlockState blockState, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl, CallbackInfo ci) {
        //#endif
        if ("true".equals(IGNYSettings.EASY_PLACE_NO_BLOCK_UPDATE.value()) && IGNYSettings.easyPlaceProtocolActive.get()) {
            ci.cancel();
        }
    }
}
