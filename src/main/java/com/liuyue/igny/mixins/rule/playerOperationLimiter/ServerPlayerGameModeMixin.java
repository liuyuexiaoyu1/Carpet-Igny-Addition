package com.liuyue.igny.mixins.rule.playerOperationLimiter;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.rule.playerOperationLimiter.SafeServerPlayerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;


@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Final
    @Shadow
    protected ServerPlayer player;

    @Shadow
    protected ServerLevel level;

    @Shadow
    protected abstract void debugLogging(BlockPos blockPos, boolean bl, int i, String string);
    private static final String igny$instaMineReason = "insta mine";

    @Inject(
            method = "destroyAndAck",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void checkOperationLimit(BlockPos pos, int sequence, String reason, CallbackInfo ci) {
        if (!IGNYSettings.playerOperationLimiter || !reason.equals(igny$instaMineReason)) {
            return;
        }
        SafeServerPlayerEntity safe = (SafeServerPlayerEntity) this.player;
        if (!safe.igny$canBreak(player)) {
            safe.igny$addBreakCountPerTick();
            this.player.connection.send(new ClientboundBlockUpdatePacket(pos, this.level.getBlockState(pos)));
            this.debugLogging(pos, false, sequence, reason);
            ci.cancel();
        }
    }
}