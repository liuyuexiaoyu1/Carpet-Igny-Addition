package com.liuyue.igny.network.packet.config;

//#if MC >= 12006
import com.liuyue.igny.network.packet.PacketUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//#endif

public record SyncLinkedEnderChestPayload(String key)
        //#if MC >= 12006
        implements CustomPacketPayload
        //#endif
{
    //#if MC >= 12006
    public static final Type<SyncLinkedEnderChestPayload> TYPE = PacketUtil.createId("sync_linked_ender_chest");

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncLinkedEnderChestPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public @NotNull SyncLinkedEnderChestPayload decode(RegistryFriendlyByteBuf buf) {
                    return new SyncLinkedEnderChestPayload(buf.readUtf());
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, SyncLinkedEnderChestPayload value) {
                    buf.writeUtf(value.key());
                }
            };
    //#endif
}