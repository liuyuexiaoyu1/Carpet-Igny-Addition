package com.liuyue.igny.network.packet.block;

//#if MC >= 12005

import com.liuyue.igny.network.packet.PacketUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//#endif
import net.minecraft.core.BlockPos;

public record RemoveHighlightPayload(BlockPos pos)
        //#if MC >= 12005
        implements CustomPacketPayload
        //#endif
{
    //#if MC >= 12005
    public static final Type<RemoveHighlightPayload> TYPE = PacketUtil.createId("remove_highlight_block");

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveHighlightPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public @NotNull RemoveHighlightPayload decode(RegistryFriendlyByteBuf buf) {
                    BlockPos blockPos = buf.readBlockPos();
                    return new RemoveHighlightPayload(blockPos);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, RemoveHighlightPayload value) {
                    buf.writeBlockPos(value.pos);
                }
            };
    //#endif
}
