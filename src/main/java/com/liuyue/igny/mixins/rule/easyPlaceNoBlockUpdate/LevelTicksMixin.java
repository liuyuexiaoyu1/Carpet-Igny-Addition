package com.liuyue.igny.mixins.rule.easyPlaceNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ScheduledTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 仅在 easyPlaceNoBlockUpdate=true 时生效：
 * 阻止协议放置期间的计划刻调度。
 */
@Mixin(LevelTicks.class)
public class LevelTicksMixin {
    @Inject(method = "schedule", at = @At("HEAD"), cancellable = true)
    private void schedule(ScheduledTick<?> scheduledTick, CallbackInfo ci) {
        if ("true".equals(IGNYSettings.EASY_PLACE_NO_BLOCK_UPDATE.value()) && IGNYSettings.easyPlaceProtocolActive.get()) {
            ci.cancel();
        }
    }
}
