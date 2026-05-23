package com.liuyue.igny.mixins.rule.ghostEnderPearlFix;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
//#if MC >= 12106
//$$ import io.netty.channel.ChannelFutureListener;
//#endif

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {
    @Shadow
    @Final
    protected Connection connection;

    @WrapOperation(
            method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V",
            //#if MC >= 12106
            //$$ at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;)V")
            //#else
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V")
            //#endif
    )
    //#if MC >= 12106
    //$$ private void wrapSend(Connection instance, Packet<?> packet, ChannelFutureListener listener, Operation<Void> original)
    //#else
    private void wrapSend(Connection instance, Packet<?> packet, PacketSendListener listener, Operation<Void> original)
    //#endif
    {
        if (!IGNYSettings.GHOST_ENDER_PEARL_FIX.value()) {
            original.call(connection, packet, listener);
            return;
        }
        Channel channel = ((ConnectionAccessor) connection).getChannel();
        original.call(connection, packet, listener);
        if (channel != null) {
            channel.closeFuture().awaitUninterruptibly(5000);
        }
    }
}
