package com.liuyue.igny.network.packet.block;

//#if MC >= 12005
import com.liuyue.igny.network.packet.PacketUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//#endif
import net.minecraft.core.BlockPos;
public record HighlightPayload(BlockPos pos, int color, int durationTicks, boolean seeThrough, boolean permanent)
        //#if MC >= 12005
        implements CustomPacketPayload
        //#endif
{
    //#if MC >= 12005
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
                    boolean seeThrough = buf.readBoolean();
                    boolean permanent = buf.readBoolean();
                    return new HighlightPayload(blockPos, color, durationTicks, seeThrough, permanent);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, HighlightPayload value) {
                    buf.writeBlockPos(value.pos);
                    buf.writeInt(value.color);
                    buf.writeInt(value.durationTicks);
                    buf.writeBoolean(value.seeThrough);
                    buf.writeBoolean(value.permanent);
                }
            };
    //#endif
}
