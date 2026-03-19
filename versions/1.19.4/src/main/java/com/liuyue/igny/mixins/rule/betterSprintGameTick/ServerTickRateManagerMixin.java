package com.liuyue.igny.mixins.rule.betterSprintGameTick;

import carpet.helpers.TickSpeed;
import com.liuyue.igny.utils.TickUtil;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TickSpeed.class)
public class ServerTickRateManagerMixin {
    @Unique private static long cachedWarpStartTime = 0;
    @Inject(method = "continueWarp", at = @At(value = "HEAD"), cancellable = true)
    private static void continueWarp(CallbackInfoReturnable<Boolean> cir) {
        if (TickSpeed.tick_warp_sender != null) {
            MinecraftServer server = TickSpeed.tick_warp_sender.getServer();
            if (!TickUtil.shouldSprint(server)) {
                if (TickSpeed.time_warp_start_time != 0) {
                    cachedWarpStartTime = TickSpeed.time_warp_start_time;
                    TickSpeed.time_warp_start_time = 0;
                }
                cir.setReturnValue(false);
            } else {
                if (TickSpeed.time_warp_start_time == 0 && cachedWarpStartTime != 0) {
                    TickSpeed.time_warp_start_time = cachedWarpStartTime;
                    cachedWarpStartTime = 0;
                }
            }
        }
    }
}
