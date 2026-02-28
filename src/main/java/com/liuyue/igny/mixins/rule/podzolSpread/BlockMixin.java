package com.liuyue.igny.mixins.rule.podzolSpread;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public abstract class BlockMixin {
    @Inject(method = "isRandomlyTicking", at = @At("HEAD"), cancellable = true)
    private void enableTicking(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.is(Blocks.PODZOL)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        if (IGNYSettings.podzolSpread && blockState.is(Blocks.PODZOL)) {
            if (serverLevel.getMaxLocalRawBrightness(blockPos.above()) < 9) {
                serverLevel.setBlockAndUpdate(blockPos, Blocks.DIRT.defaultBlockState());
                return;
            }
            if (serverLevel.getMaxLocalRawBrightness(blockPos.above()) >= 9) {
                for (int i = 0; i < 4; i++) {
                    BlockPos targetPos = blockPos.offset(
                            randomSource.nextInt(3) - 1,
                            randomSource.nextInt(5) - 3,
                            randomSource.nextInt(3) - 1
                    );

                    BlockState targetState = serverLevel.getBlockState(targetPos);

                    if (targetState.is(Blocks.DIRT) && serverLevel.getBlockState(targetPos.above()).getFluidState().isEmpty()) {
                        serverLevel.setBlockAndUpdate(targetPos, Blocks.PODZOL.defaultBlockState());
                    }
                }
            }
        }
    }
}
