package com.liuyue.igny.mixins.carpet.fix.fakePlayerMemoryLeakFix;

import carpet.patches.NetHandlerPlayServerFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie; //?>= 1.20.2

@Mixin(NetHandlerPlayServerFake.class)
public class NetHandlerPlayServerFakeMixin extends ServerGamePacketListenerImpl {
    public NetHandlerPlayServerFakeMixin(MinecraftServer server, Connection connection, ServerPlayer player, CommonListenerCookie cookie) //#replace < 1.20.2 ? public NetHandlerPlayServerFakeMixin(MinecraftServer server, Connection connection, ServerPlayer player)
    {
        super(server, connection, player, cookie); //#replace < 1.20.2 ? super(server, connection, player);
    }

    @Inject(method = "send", at = @At(value = "HEAD"))
    private void send(Packet<?> packetIn, CallbackInfo ci) {
        if (IGNYSettings.FAKE_PLAYER_MEMORY_LEAK_FIX.value()) {
            super.send(packetIn);
        }
    }
}
