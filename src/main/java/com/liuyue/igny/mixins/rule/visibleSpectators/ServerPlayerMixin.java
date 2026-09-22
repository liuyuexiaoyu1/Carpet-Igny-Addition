package com.liuyue.igny.mixins.rule.visibleSpectators;

import com.liuyue.igny.IGNYSettings;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos; //?< 1.21.6
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) //#replace >= 1.21.6 ? public ServerPlayerMixin(Level level, GameProfile gameProfile)
    {
        super(level, pos, yRot, gameProfile); //#replace >= 1.21.6 ? super(level, gameProfile);
    }

    @Shadow
    public abstract @NotNull Entity getCamera();

    @Inject(method = "updateInvisibilityStatus", at = @At(value = "HEAD"), cancellable = true)
    private void updateInvisibilityStatus(CallbackInfo ci) {
        if (IGNYSettings.VISIBLE_SPECTATORS.value()) {
            if (this.isSpectator()) {
                this.removeEffectParticles();
                this.setInvisible(this.getCamera() != this);
            } else {
                super.updateInvisibilityStatus();
            }
            ci.cancel();
        }
    }

    @Inject(method = "broadcastToPlayer", at = @At(value = "HEAD"), cancellable = true)
    private void allowSpectatorsToBeSpectated(ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.VISIBLE_SPECTATORS.value()) {
            if (player.isSpectator()) {
                cir.setReturnValue(this.getCamera() == this);
            } else {
                cir.setReturnValue(super.broadcastToPlayer(player));
            }
        }
    }

    @Inject(method = "setCamera", at = @At(value = "RETURN"))
    private void onSetCamera(Entity entity, CallbackInfo ci) {
        if (IGNYSettings.VISIBLE_SPECTATORS.value()) {
            this.updateInvisibilityStatus();
        }
    }
}
