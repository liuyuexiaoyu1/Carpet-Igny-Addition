package com.liuyue.igny.mixins.logger;

import carpet.CarpetServer;
import carpet.logging.Logger;
import com.liuyue.igny.logging.IGNYLoggers;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Logger.class)
public class LoggerMixin {
    @Shadow
    private Map<String, String> subscribedOnlinePlayers;

    @Inject(method = "addPlayer", at = @At("HEAD"))
    private void addPlayer(String playerName, String option, CallbackInfo ci) {
        IGNYLoggers.handleChange(CarpetServer.minecraft_server,(Logger) (Object) this, playerName, option, true);
    }

    @Inject(method = "removePlayer", at = @At("HEAD"))
    private void removePlayer(String playerName, CallbackInfo ci) {
        IGNYLoggers.handleChange(CarpetServer.minecraft_server,(Logger) (Object) this, playerName, null, false);
    }

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerConnect(Player player, boolean firstTime, CallbackInfo ci) {
        String playerName = player.getGameProfile().getName();
        if (subscribedOnlinePlayers.containsKey(playerName)) {
            IGNYLoggers.handleChange(CarpetServer.minecraft_server, (Logger) (Object) this, playerName, this.subscribedOnlinePlayers.get(playerName), true);
        }
    }

    @Inject(method = "onPlayerDisconnect", at = @At("RETURN"))
    private void onPlayerDisconnect(Player player, CallbackInfo ci) {
        String playerName = player.getGameProfile().getName();
        IGNYLoggers.handleChange(CarpetServer.minecraft_server,(Logger) (Object) this, playerName, null, false);
    }
}
