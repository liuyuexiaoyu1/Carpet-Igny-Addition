package com.liuyue.igny.mixins.rule.noCreativeDestroyAttachmentDrops;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At(value = "HEAD"), cancellable = true)
    private static void dropResources(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        if (IGNYSettings.noCreativeDestroyAttachmentDrops && IGNYSettings.CREATIVE_BREAKING.get()) ci.cancel();
    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At(value = "HEAD"), cancellable = true)
    private static void dropResources(BlockState state, LevelAccessor level, BlockPos pos, BlockEntity blockEntity, CallbackInfo ci) {
        if (IGNYSettings.noCreativeDestroyAttachmentDrops && IGNYSettings.CREATIVE_BREAKING.get()) ci.cancel();
    }
}
