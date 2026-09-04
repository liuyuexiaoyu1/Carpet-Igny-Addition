package com.liuyue.igny.mixins.rule.noCreativeDestroyAttachmentDrops;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
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
    @Inject(method = "popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"), cancellable = true)
    private static void dropResources(Level level, BlockPos pos, ItemStack stack, CallbackInfo ci) {
        if (IGNYSettings.NO_CREATIVE_DESTROY_ATTACHMENT_DROPS.value() && IGNYSettings.CREATIVE_BREAKING.get()) ci.cancel();
    }
}
