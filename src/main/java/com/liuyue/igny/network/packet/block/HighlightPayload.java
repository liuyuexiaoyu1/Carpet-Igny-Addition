package com.liuyue.igny.network.packet.block;

//#if >= 1.20.5
import com.liuyue.igny.network.PacketUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//#endif
import net.minecraft.core.BlockPos;
public record HighlightPayload(BlockPos pos, int color, int durationTicks, boolean permanent)
        implements CustomPacketPayload //?>= 1.20.5
{
    //#if >= 1.20.5
    public static final Type<HighlightPayload> TYPE = PacketUtil.createId("highlight_block");

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}

    public static final StreamCodec<RegistryFriendlyByteBuf, HighlightPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public @NotNull HighlightPayload decode(RegistryFriendlyByteBuf buf) {
                    BlockPos blockPos = buf.readBlockPos();
                    int color = buf.readInt();
                    int durationTicks = buf.readInt();
                    boolean permanent = buf.readBoolean();
                    return new HighlightPayload(blockPos, color, durationTicks, permanent);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, HighlightPayload value) {
                    buf.writeBlockPos(value.pos);
                    buf.writeInt(value.color);
                    buf.writeInt(value.durationTicks);
                    buf.writeBoolean(value.permanent);
                }
            };
    //#endif
}
