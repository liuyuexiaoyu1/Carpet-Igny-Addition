package com.liuyue.igny.network.packet.config;

//#if MC >= 12006
import com.liuyue.igny.network.PacketUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//#endif

import java.util.HashMap;
import java.util.Map;

public record SyncCustomStackSizePayload(Map<String, Integer> customStacks)
        //#if MC >= 12006
        implements CustomPacketPayload
        //#endif
{
    //#if MC >= 12006
    public static final Type<SyncCustomStackSizePayload> TYPE = PacketUtil.createId("sync_custom_stack_size");

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCustomStackSizePayload> CODEC =
            new StreamCodec<>() {
                @Override
                public @NotNull SyncCustomStackSizePayload decode(RegistryFriendlyByteBuf buf) {
                    int size = buf.readVarInt();
                    Map<String, Integer> map = new HashMap<>(size);
                    for (int i = 0; i < size; i++) {
                        map.put(buf.readUtf(), buf.readVarInt());
                    }
                    return new SyncCustomStackSizePayload(map);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, SyncCustomStackSizePayload value) {
                    buf.writeVarInt(value.customStacks.size());
                    value.customStacks.forEach((id, count) -> {
                        buf.writeUtf(id);
                        buf.writeVarInt(count);
                    });
                }
            };
    //#endif
}