package com.liuyue.igny.mixins.rule.fakePlayerMemoryLeakFix;

//#if MC < 26.1
import carpet.patches.NetHandlerPlayServerFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
//$$ import com.liuyue.igny.utils.compat.DummyClass;
//#endif
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 26.1
//$$ @Mixin(DummyClass.class)
//#else
@Mixin(NetHandlerPlayServerFake.class)
//#endif
public class NetHandlerPlayServerFakeMixin {
    //#if MC < 26.1
    @Inject(method = "send", at = @At(value = "HEAD"))
    private void send(Packet<?> packetIn, CallbackInfo ci) {
        if (IGNYSettings.fakePlayerMemoryLeakFix) {
            ServerGamePacketListenerImpl connection = (ServerGamePacketListenerImpl) (Object) this;
            connection.send(packetIn);
        }
    }
    //#endif
}
