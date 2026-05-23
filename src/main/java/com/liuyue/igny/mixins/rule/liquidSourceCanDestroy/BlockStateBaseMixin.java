package com.liuyue.igny.mixins.rule.liquidSourceCanDestroy;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {
    @Inject(method = "getDestroyProgress", at = @At(value = "HEAD"), cancellable = true)
    private void getDestroyProgress(Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;
        if (IGNYSettings.LIQUID_SOURCE_CAN_DESTROY.value() && state instanceof BlockState blockState && blockState.getBlock() instanceof LiquidBlock) {
            cir.setReturnValue(player.getDestroySpeed(blockState) / 10.0F);
        }
    }
}
