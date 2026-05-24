package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {
    @WrapWithCondition(method = "preRemoveSideEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropContents(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/Container;)V"))
    private boolean preRemoveSideEffects(Level level, BlockPos pos, Container inventory) {
        BlockEntity blockEntity = (BlockEntity) (Object) this;
        return !(blockEntity instanceof EnderChestBlockEntity);
    }
}
