package com.liuyue.igny.mixins.rule.renewableCalcite;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {
    @Inject(method = "maybeTransferFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static void maybeTransferFluid(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, float f, CallbackInfo ci, @Local Optional<PointedDripstoneBlock.FluidInfo> optional, @Local Fluid fluid, @Local(ordinal = 1) BlockPos blockPos2){
        if (!IGNYSettings.renewableCalcite || optional.isEmpty()) return;
        if (optional.get().sourceState().is(Blocks.QUARTZ_BLOCK) && fluid == Fluids.WATER) {
            BlockState blockState2 = Blocks.CALCITE.defaultBlockState();
            serverLevel.setBlockAndUpdate((optional.get()).pos(), blockState2);
            Block.pushEntitiesUp(optional.get().sourceState(), blockState2, serverLevel, optional.get().pos());
            serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, optional.get().pos(), GameEvent.Context.of(blockState2));
            serverLevel.levelEvent(1504, blockPos2, 0);
        }
    }

    @WrapOperation(method = "method_33279", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean is(BlockState instance, Block block, Operation<Boolean> original) {
        if (IGNYSettings.renewableCalcite) {
            return original.call(instance, block) || instance.is(Blocks.QUARTZ_BLOCK);
        }
        return original.call(instance, block);
    }
}
