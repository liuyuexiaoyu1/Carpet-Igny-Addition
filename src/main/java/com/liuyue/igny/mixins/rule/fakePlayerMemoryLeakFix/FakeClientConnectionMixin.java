package com.liuyue.igny.mixins.rule.fakePlayerMemoryLeakFix;

//#if MC < 26.1
import carpet.patches.FakeClientConnection;
//#if MC >= 12106
//$$ import io.netty.channel.ChannelFutureListener;
//#else
import net.minecraft.network.PacketSendListener;
//#endif
import com.liuyue.igny.IGNYSettings;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;
//#else
//$$ import com.liuyue.igny.utils.compat.DummyClass;
//#endif
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 26.1
//$$ @Mixin(DummyClass.class)
//#else
@Mixin(FakeClientConnection.class)
//#endif
public class FakeClientConnectionMixin
    //#if MC < 26.1
        extends Connection
    //#endif
{
    //#if MC < 26.1
    public FakeClientConnectionMixin(PacketFlow receiving) {
        super(receiving);
    }

    @Override
    //#if MC >= 12106
    //$$ public void send(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, boolean bl)
    //#else
    public void send(Packet<?> packet, @Nullable PacketSendListener channelFutureListener, boolean bl)
    //#endif
    {
        if (!IGNYSettings.fakePlayerMemoryLeakFix) super.send(packet, channelFutureListener, bl);
    }
    //#endif
}
