package com.liuyue.igny.mixins.rule.ghostEnderPearlFix;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {
    @Shadow
    @Final
    protected Connection connection;

    @WrapOperation(
            method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V")
    )
    private void wrapSend(Connection instance, Packet<?> packet, PacketSendListener packetSendListener, Operation<Void> original) {
        if (!IGNYSettings.GHOST_ENDER_PEARL_FIX.value()) {
            original.call(connection, packet, packetSendListener);
            return;
        }
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        PacketSendListener combinedListener = PacketSendListener.thenRun(latch::countDown);
        if (packetSendListener != null) {
            combinedListener = PacketSendListener.thenRun(() -> {
                packetSendListener.onSuccess();
                latch.countDown();
            });
        }
        original.call(connection, packet, combinedListener);
        try {
            latch.await(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
