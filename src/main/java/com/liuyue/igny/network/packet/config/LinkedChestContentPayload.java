package com.liuyue.igny.network.packet.config;

import net.minecraft.world.item.ItemStack;

import java.util.List;

//#if >= 1.20.5
import com.liuyue.igny.network.PacketUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//#endif

public record LinkedChestContentPayload(String key, List<ItemStack> items)
        implements CustomPacketPayload //?>= 1.20.6
{
    //#if >= 1.20.5
    public static final Type<LinkedChestContentPayload> TYPE = PacketUtil.createId("linked_chest_content");

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static final StreamCodec<RegistryFriendlyByteBuf, LinkedChestContentPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public @NotNull LinkedChestContentPayload decode(RegistryFriendlyByteBuf buf) {
                    String key = buf.readUtf();
                    List<ItemStack> items = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buf);
                    return new LinkedChestContentPayload(key, items);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, LinkedChestContentPayload value) {
                    buf.writeUtf(value.key());
                    ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buf, value.items());
                }
            };
    //#endif
}
