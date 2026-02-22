package com.liuyue.igny.mixins.rule.structureBlockNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DiodeBlock.class)
public class DiodeBlockMixin {
    @Inject(method = "checkTickOnNeighbor", at = @At("HEAD"), cancellable = true)
    private void checkTickOnNeighbor(Level level, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (IGNYSettings.structureBlockNoBlockUpdate && IGNYSettings.noUpdatePos.contains(blockPos)) {
            ci.cancel();
        }
    }
}