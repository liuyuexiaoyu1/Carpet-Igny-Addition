package com.liuyue.igny.mixins.rule.easyPlaceNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 仅在 easyPlaceNoBlockUpdate=true 时生效：
 * 阻止中继器/比较器在协议放置期间进行邻居刻检测。
 */
@Mixin(DiodeBlock.class)
public class DiodeBlockMixin {
    @Inject(method = "checkTickOnNeighbor", at = @At("HEAD"), cancellable = true)
    private void checkTickOnNeighbor(Level level, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if ("true".equals(IGNYSettings.EASY_PLACE_NO_BLOCK_UPDATE.value()) && IGNYSettings.easyPlaceProtocolActive.get()) {
            ci.cancel();
        }
    }
}
