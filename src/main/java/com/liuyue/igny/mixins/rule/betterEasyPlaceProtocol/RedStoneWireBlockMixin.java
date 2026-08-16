package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
public abstract class RedStoneWireBlockMixin {
    //#if MC < 12103
    @WrapOperation(
            method = "onPlace",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RedStoneWireBlock;updatePowerStrength(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V")
    )
    private static void igny_skipUpdatePowerStrength(RedStoneWireBlock instance, Level level, BlockPos pos, BlockState state, Operation<Void> original) {
        if (!BetterEasyPlaceProtocolHandler.isEasyPlaceState() ||
                !BetterEasyPlaceProtocolHandler.hasPlaceFlag(BetterEasyPlaceProtocolHandler.EASY_PLACE_REDSTONE_WIRE_NO_UPDATE)) {
            original.call(instance, level, pos, state);
            return;
        }
        original.call(instance, level, pos, state);
    //#else
    //$$ @WrapOperation(
    //$$         method = "onPlace",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RedStoneWireBlock;updatePowerStrength(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/redstone/Orientation;Z)V")
    //$$ )
    //$$ private static void igny_skipUpdatePowerStrength(Level level, BlockPos pos, BlockState state, net.minecraft.world.level.redstone.Orientation orientation, boolean flag, Operation<Void> original) {
    //$$     if (!BetterEasyPlaceProtocolHandler.isEasyPlaceState() ||
    //$$             !BetterEasyPlaceProtocolHandler.hasPlaceFlag(BetterEasyPlaceProtocolHandler.EASY_PLACE_REDSTONE_WIRE_NO_UPDATE)) {
    //$$         original.call(level, pos, state, orientation, flag);
    //$$         return;
    //$$     }
    //$$
    //$$     original.call(level, pos, state, orientation, flag);
    //#endif

        
        long prop = BetterEasyPlaceProtocolHandler.getPlaceProperty();
        BlockState current = level.getBlockState(pos);
        if (!current.is(Blocks.REDSTONE_WIRE)) {
            return;
        }
        BlockState restored = current
                .setValue(RedStoneWireBlock.NORTH, RedstoneSide.values()[(int) ((prop >>> 4) & 0b11)])
                .setValue(RedStoneWireBlock.EAST, RedstoneSide.values()[(int) ((prop >>> 6) & 0b11)])
                .setValue(RedStoneWireBlock.SOUTH, RedstoneSide.values()[(int) ((prop >>> 8) & 0b11)])
                .setValue(RedStoneWireBlock.WEST, RedstoneSide.values()[(int) ((prop >>> 10) & 0b11)]);
        level.setBlock(pos, restored, 3);
    }

    //#if MC >= 12103
    //$$ @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    //$$ private void igny_cancelNeighborUpdate(BlockState state, Level world, BlockPos pos, Block block, net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving, CallbackInfo ci) {
    //#else
    @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    private void igny_cancelNeighborUpdate(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving, CallbackInfo ci) {
    //#endif
        if (!BetterEasyPlaceProtocolHandler.isEasyPlaceState()) {
            return;
        }
        ci.cancel();
    }

    //#if MC >= 12102
    //$$ @Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
    //$$ private void igny_cancelUpdateShape(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.world.level.ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, net.minecraft.util.RandomSource randomSource, CallbackInfoReturnable<BlockState> cir) {
    //#else
    @Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
    private void igny_cancelUpdateShape(BlockState state, Direction direction, BlockState neighborState, net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
    //#endif
        if (!BetterEasyPlaceProtocolHandler.isEasyPlaceState()) {
            return;
        }
        cir.setReturnValue(state);
    }
}