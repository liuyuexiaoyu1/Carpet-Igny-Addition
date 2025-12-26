package com.liuyue.igny.mixins.features.rule.rule.locatorBarNoFakePlayer;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.waypoints.ServerWaypointManager;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.liuyue.igny.IGNYSettings;

import java.util.HashSet;
import java.util.Set;

@Mixin(ServerWaypointManager.class)
public abstract class ServerWaypointManagerMixin {

    @Shadow
    @Final
    private Set<ServerPlayer> players;

    @Shadow
    public abstract void removePlayer(ServerPlayer serverPlayer);

    @Shadow
    @Final
    private Set<WaypointTransmitter> waypoints;

    @Shadow
    public abstract void untrackWaypoint(WaypointTransmitter waypointTransmitter);

    @Shadow
    public abstract void addPlayer(ServerPlayer serverPlayer);

    @Inject(method = "createConnection", at = @At("HEAD"), cancellable = true)
    private void onCreateConnection(ServerPlayer serverPlayer,
                                    WaypointTransmitter waypointTransmitter,
                                    CallbackInfo ci) {
        if (waypointTransmitter instanceof EntityPlayerMPFake && IGNYSettings.locatorBarNoFakePlayer) {
            ci.cancel();
        }
    }

    @Inject(method = "updateConnection", at = @At("HEAD"), cancellable = true)
    private void onUpdateConnection(ServerPlayer serverPlayer,
                                    WaypointTransmitter waypointTransmitter,
                                    WaypointTransmitter.Connection connection,
                                    CallbackInfo ci) {

        if (IGNYSettings.locatorBarNoFakePlayer) {
            if (waypointTransmitter instanceof EntityPlayerMPFake) {
                ci.cancel();
            }
            this.cleanupFakePlayerPlayers();
            this.cleanupFakePlayerWaypoints();
        }else {
            this.ensureAllFakePlayersAreTracked(serverPlayer);
        }
    }

    @Inject(method = "addPlayer", at = @At("HEAD"), cancellable = true)
    private void onAddPlayer(ServerPlayer serverPlayer, CallbackInfo ci) {
        if (serverPlayer instanceof EntityPlayerMPFake && IGNYSettings.locatorBarNoFakePlayer) {
            ci.cancel();
        }
    }

    @Inject(method = "trackWaypoint(Lnet/minecraft/world/waypoints/WaypointTransmitter;)V", at = @At("HEAD"), cancellable = true)
    private void onTrackWaypoint(WaypointTransmitter waypointTransmitter, CallbackInfo ci) {
        if (waypointTransmitter instanceof EntityPlayerMPFake && IGNYSettings.locatorBarNoFakePlayer) {
            ci.cancel();
        }
    }

    @Unique
    private void cleanupFakePlayerPlayers() {
        Set<ServerPlayer> botsToRemove = new HashSet<>();

        for (ServerPlayer player : this.players) {
            if (player instanceof EntityPlayerMPFake) {
                botsToRemove.add(player);
            }
        }

        for (ServerPlayer bot : botsToRemove) {
            this.removePlayer(bot);
        }
    }

    @Unique
    private void cleanupFakePlayerWaypoints() {
        Set<WaypointTransmitter> botWaypointsToRemove = new HashSet<>();

        for (WaypointTransmitter transmitter : this.waypoints) {
            if (transmitter instanceof EntityPlayerMPFake) {
                botWaypointsToRemove.add(transmitter);
            }
        }

        for (WaypointTransmitter botTransmitter : botWaypointsToRemove) {
            this.untrackWaypoint(botTransmitter);
        }
    }

    @Unique
    private void ensureAllFakePlayersAreTracked(ServerPlayer serverPlayer) {
        MinecraftServer server = serverPlayer.level().getServer();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player instanceof EntityPlayerMPFake fakePlayer) {
                if (!this.players.contains(fakePlayer)) {
                    this.addPlayer(fakePlayer);
                }
            }
        }
    }
}

