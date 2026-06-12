package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {
    @Inject(method = "getBlockEntity", at = @At(value = "HEAD"), cancellable = true)
    private void getBlockEntity(BlockPos pos, CallbackInfoReturnable<BlockEntity> cir) {
        if (!IGNYSettings.canGetBlockEntity.get()) cir.setReturnValue(null);
    }
}
