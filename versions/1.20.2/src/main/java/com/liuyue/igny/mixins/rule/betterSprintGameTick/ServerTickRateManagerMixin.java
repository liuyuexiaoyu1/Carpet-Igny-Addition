package com.liuyue.igny.mixins.rule.betterSprintGameTick;

import carpet.helpers.ServerTickRateManager;
import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.liuyue.igny.utils.TickUtil;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerTickRateManager.class)
public class ServerTickRateManagerMixin {
    @Shadow private MinecraftServer server;

    @WrapMethod(method = "isInWarpSpeed")
    private boolean isInWarpSpeed(Operation<Boolean> original) {
        return TickUtil.shouldSprint(this.server) && original.call();
    }

    @Inject(method = "continueWarp", at = @At(value = "HEAD"), cancellable = true)
    private void checkShouldSprintThisTick(CallbackInfoReturnable<Boolean> cir) {
        if (!TickUtil.shouldSprint(this.server)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "setTickRate(FZ)V", at = @At(value = "HEAD"), cancellable = true)
    private void setTickRate(float rate, boolean update, CallbackInfo ci) {
        if (!TickUtil.shouldSprint(this.server)) {
            ci.cancel();
        }
    }

    @Inject(method = "requestGameToWarpSpeed", at = @At(value = "RETURN"))
    private void requestGameToWarpSpeed(ServerPlayer player, int advance, String callback, CommandSourceStack source, CallbackInfoReturnable<Component> cir) {
        for (ServerPlayer player2 : this.server.getPlayerList().getPlayers()) {
            IGNYSettings.sprintWhitelistPlayers.add(player2.getUUID());
        }
    }

    @Inject(method = "finishTickWarp", at = @At(value = "RETURN"))
    private static void finishTickWarp(CallbackInfo ci) {
        IGNYSettings.sprintWhitelistPlayers.clear();
    }
}
