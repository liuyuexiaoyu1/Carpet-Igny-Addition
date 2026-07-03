package com.liuyue.igny.mixins.rule.easyPlaceNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 仅在 easyPlaceNoBlockUpdate=true 时生效：
 * 阻止协议放置期间的方块事件（如活塞声音/动画）。
 */
@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "blockEvent", at = @At("HEAD"), cancellable = true)
    private void blockEvent(BlockPos blockPos, Block block, int i, int j, CallbackInfo ci) {
        if ("true".equals(IGNYSettings.EASY_PLACE_NO_BLOCK_UPDATE.value()) && IGNYSettings.easyPlaceProtocolActive.get()) {
            ci.cancel();
        }
    }
}
