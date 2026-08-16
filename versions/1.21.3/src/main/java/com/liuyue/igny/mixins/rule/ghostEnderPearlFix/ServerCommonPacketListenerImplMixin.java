package com.liuyue.igny.mixins.rule.ghostEnderPearlFix;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {
    @Shadow
    @Final
    protected Connection connection;

    @Shadow
    @Final
    protected MinecraftServer server;

    @Inject(method = "method_60674", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;disconnect(Lnet/minecraft/network/DisconnectionDetails;)V"))
    private void beforeDisconnect(DisconnectionDetails disconnectionDetails, CallbackInfo ci) {
        if (IGNYSettings.GHOST_ENDER_PEARL_FIX.value()) {
            this.connection.setReadOnly();
            this.server.executeBlocking(this.connection::handleDisconnection);
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setReadOnly()V"), cancellable = true)
    private void disconnect(DisconnectionDetails disconnectionDetails, CallbackInfo ci) {
        if (IGNYSettings.GHOST_ENDER_PEARL_FIX.value()) {
            ci.cancel();
        }
    }
}
