package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RepeaterBlock.class, priority = 900)
public abstract class RepeaterBlockMixin {
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
        if (pos.equals(BetterEasyPlaceProtocolHandler.getPlaceTargetPos())) {
            cir.setReturnValue(state);
        }
    }
}
