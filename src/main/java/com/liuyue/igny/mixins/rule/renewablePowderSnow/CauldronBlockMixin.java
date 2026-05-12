package com.liuyue.igny.mixins.rule.renewablePowderSnow;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.level.gameevent.GameEvent;

@Mixin(CauldronBlock.class)
public class CauldronBlockMixin {
    @Inject(method = "receiveStalactiteDrip", at = @At(value = "HEAD"), cancellable = true)
    private void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid, CallbackInfo ci) {
        if (IGNYSettings.renewablePowderSnow && state.is(Blocks.POWDER_SNOW)) {
            BlockState powderSnowState = Blocks.POWDER_SNOW_CAULDRON.defaultBlockState();
            level.setBlockAndUpdate(pos, powderSnowState);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(powderSnowState));
            level.levelEvent(1047, pos, 0);
            ci.cancel();
        }
    }
}
