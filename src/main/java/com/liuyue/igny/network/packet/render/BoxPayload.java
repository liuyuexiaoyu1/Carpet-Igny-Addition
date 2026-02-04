package com.liuyue.igny.network.packet.render;

//#if MC >= 12005
import com.liuyue.igny.network.packet.PacketUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//#endif
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;

public record BoxPayload(
        BlockPos pos,
        int color,
        int durationTicks,
        boolean permanent,
        boolean depthTest,
        AABB box,
        boolean withLine,
        boolean lineDepthTest,
        boolean smooth
)
        //#if MC >= 12005
        implements CustomPacketPayload
        //#endif
{
    //#if MC >= 12005
    public static final Type<BoxPayload> TYPE = PacketUtil.createId("render_box");

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static final StreamCodec<RegistryFriendlyByteBuf, BoxPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public @NotNull BoxPayload decode(RegistryFriendlyByteBuf buf) {
                    return new BoxPayload(
                            buf.readBlockPos(), buf.readInt(), buf.readInt(),
                            buf.readBoolean(), buf.readBoolean(),
                            new AABB(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble()),
                            buf.readBoolean(), buf.readBoolean(), buf.readBoolean()
                    );
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, BoxPayload v) {
                    buf.writeBlockPos(v.pos);
                    buf.writeInt(v.color);
                    buf.writeInt(v.durationTicks);
                    buf.writeBoolean(v.permanent);
                    buf.writeBoolean(v.depthTest);
                    AABB box = v.box();
                    buf.writeDouble(box.minX);
                    buf.writeDouble(box.minY);
                    buf.writeDouble(box.minZ);
                    buf.writeDouble(box.maxX);
                    buf.writeDouble(box.maxY);
                    buf.writeDouble(box.maxZ);
                    buf.writeBoolean(v.withLine);
                    buf.writeBoolean(v.lineDepthTest);
                    buf.writeBoolean(v.smooth);
                }
            };
    //#endif
}