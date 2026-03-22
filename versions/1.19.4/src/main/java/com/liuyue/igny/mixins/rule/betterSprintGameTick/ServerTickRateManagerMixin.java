package com.liuyue.igny.mixins.rule.betterSprintGameTick;

import carpet.helpers.TickSpeed;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.TickUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(method = "tickrate(FZ)V", at = @At(value = "HEAD"), cancellable = true)
    private static void setTickRate(float rate, boolean update, CallbackInfo ci) {
        if (TickSpeed.tick_warp_sender != null) {
            MinecraftServer server = TickSpeed.tick_warp_sender.getServer();
            if (!TickUtil.shouldSprint(server)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "tickrate_advance", at = @At(value = "RETURN"))
    private static void tickrate_advance(ServerPlayer player, int advance, String callback, CommandSourceStack source, CallbackInfoReturnable<Component> cir) {
        if (TickSpeed.tick_warp_sender != null) {
            MinecraftServer server = TickSpeed.tick_warp_sender.getServer();
            for (ServerPlayer player2 : server.getPlayerList().getPlayers()) {
                IGNYSettings.sprintWhitelistPlayers.add(player2.getUUID());
            }
        }
    }

    @Inject(method = "finish_time_warp", at = @At(value = "RETURN"))
    private static void finish_time_warp(CallbackInfo ci) {
        IGNYSettings.sprintWhitelistPlayers.clear();
    }
}
