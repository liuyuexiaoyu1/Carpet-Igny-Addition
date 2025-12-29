package com.liuyue.igny.mixins.features.rule.furnaceHasIncombustibleHighlight;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.network.packet.block.RemoveHighlightPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 12102
//$$ import net.minecraft.server.level.ServerLevel;
//#endif

//#if MC < 12005
//$$ import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import com.liuyue.igny.IGNYServer;
//#endif

@Mixin(Level.class)
public abstract class LevelMixin {
    @Shadow
    @Nullable
    public abstract BlockEntity getBlockEntity(BlockPos blockPos);

    @Inject(method = "removeBlockEntity", at = @At(value = "HEAD"))
    private void removeBlockEntity(BlockPos blockPos, CallbackInfo ci) {
        if (IGNYSettings.furnaceHasIncombustibleHighlight && this.getBlockEntity(blockPos) instanceof AbstractFurnaceBlockEntity) {
            Level level = (Level) (Object) this;
            this.removeHighlightToClient(
                    //#if MC >= 12102
                    //$$ (ServerLevel)
                    //#endif
                            level, blockPos);
        }
    }

    @Unique
    private void removeHighlightToClient(
            //#if MC >= 12102
            //$$ ServerLevel level,
            //#else
            Level level,
            //#endif
            BlockPos pos) {
        if (!level.isClientSide()) {
            //#if MC < 12005
            //$$ FriendlyByteBuf buf = PacketByteBufs.create();
            //$$ buf.writeBlockPos(pos);
            //#endif
            level.players().stream()
                    .filter(player -> player instanceof ServerPlayer)
                    .forEach(player -> {
                        if (ServerPlayNetworking.canSend((ServerPlayer) player,
                                //#if MC >= 12005
                                RemoveHighlightPayload.TYPE
                                //#else
                                //$$ IGNYServer.REMOVE_HIGHLIGHT_PACKET_ID
                                //#endif

                        )) {
                            ServerPlayNetworking.send(
                                    (ServerPlayer) player,
                                    //#if MC >= 12005
                                    new RemoveHighlightPayload(pos)
                                    //#else
                                    //$$ IGNYServer.REMOVE_HIGHLIGHT_PACKET_ID,
                                    //$$ buf
                                    //#endif
                            );
                        }
                    });
        }
    }
}
