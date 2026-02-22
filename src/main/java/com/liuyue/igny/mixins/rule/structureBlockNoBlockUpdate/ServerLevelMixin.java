package com.liuyue.igny.mixins.rule.structureBlockNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "blockEvent", at = @At("HEAD"), cancellable = true)
    private void blockEvent(BlockPos blockPos, Block block, int i, int j, CallbackInfo ci) {
        if (IGNYSettings.structureBlockNoBlockUpdate && IGNYSettings.noUpdatePos.contains(blockPos)) ci.cancel();
    }
}
