package com.liuyue.igny.network.packet.entity;

//#if MC >= 12005
import com.liuyue.igny.network.PacketUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//#endif
import net.minecraft.resources.ResourceLocation;

public record PlaceEntityPayload(
        ResourceLocation entityTypeId,
        double x, double y, double z,
        float yaw, float yawHead, float pitch
)
        //#if MC >= 12005
        implements CustomPacketPayload
        //#endif
{
    //#if MC >= 12005
    public static final Type<PlaceEntityPayload> TYPE = PacketUtil.createId("place_entity");

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, PlaceEntityPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public @NotNull PlaceEntityPayload decode(RegistryFriendlyByteBuf buf) {
                    return new PlaceEntityPayload(
                            //#if MC >= 26.1
                            //$$ buf.readIdentifier(),
                            //#else
                            buf.readResourceLocation(),
                            //#endif
                            buf.readDouble(), buf.readDouble(), buf.readDouble(),
                            buf.readFloat(), buf.readFloat(), buf.readFloat()
                    );
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, PlaceEntityPayload value) {
                    //#if MC >= 26.1
                    //$$ buf.writeIdentifier(value.entityTypeId());
                    //#else
                    buf.writeResourceLocation(value.entityTypeId());
                    //#endif
                    buf.writeDouble(value.x());
                    buf.writeDouble(value.y());
                    buf.writeDouble(value.z());
                    buf.writeFloat(value.yaw());
                    buf.writeFloat(value.yawHead());
                    buf.writeFloat(value.pitch());
                }
            };
    //#endif
}
