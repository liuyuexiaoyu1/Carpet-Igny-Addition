package com.liuyue.igny.mixins.rule.drillAnvil;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    @Shadow
    private BlockState blockState;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;canSurvive(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z", shift = At.Shift.AFTER), cancellable = true)
    private void tick(CallbackInfo ci, @Local BlockPos blockPos, @Local(ordinal = 2) boolean bl3) {
        if (!IGNYSettings.drillAnvil.equals("false") && this.blockState.is(BlockTags.ANVIL)) {
            FallingBlockEntity entity = (FallingBlockEntity) (Object) this;
            BlockPos pos = bl3 ? blockPos.below() : blockPos;
            BlockState blockState = entity.level().getBlockState(pos);
            if (!blockState.is(Blocks.AIR) && !blockState.is(BlockTags.ANVIL)) {
                float hardness = blockState.getDestroySpeed(entity.level(), pos);
                float blastResistance = blockState.getBlock().getExplosionResistance();
                if (hardness < 0 || blastResistance >= 1200) return;
                if (blockState.getFluidState().isEmpty() || IGNYSettings.drillAnvil.equals("true")) {
                    entity.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    entity.setDeltaMovement(entity.getDeltaMovement().scale(0.98));
                    ci.cancel();
                }
            }
        }
    }
}
