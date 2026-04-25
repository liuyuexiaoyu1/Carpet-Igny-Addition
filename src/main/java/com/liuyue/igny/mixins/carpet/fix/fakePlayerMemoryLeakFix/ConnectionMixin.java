package com.liuyue.igny.mixins.carpet.fix.fakePlayerMemoryLeakFix;

import carpet.patches.FakeClientConnection;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 12106
//$$ import io.netty.channel.ChannelFutureListener;
//#endif
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Connection.class)
public class ConnectionMixin {
    //#if MC >= 12106
    //$$ @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V", at = @At("HEAD"), cancellable = true)
    //#elseif MC >= 12002
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;Z)V", at = @At("HEAD"), cancellable = true)
    //#else
    //$$ @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V", at = @At("HEAD"), cancellable = true)
    //#endif
    //#if MC >= 12106
    //$$ private void sendPacket(Packet<?> packet, ChannelFutureListener sendListener, boolean flush, CallbackInfo ci)
    //#elseif MC >= 12002
    private void sendPacket(Packet<?> packet, PacketSendListener sendListener, boolean flush, CallbackInfo ci)
    //#else
    //$$ private void sendPacket(Packet<?> packet, PacketSendListener sendListener, CallbackInfo ci)
    //#endif
    {
        Connection self = (Connection) (Object) this;
        if (IGNYSettings.fakePlayerMemoryLeakFix && self instanceof FakeClientConnection) ci.cancel();
    }
}
