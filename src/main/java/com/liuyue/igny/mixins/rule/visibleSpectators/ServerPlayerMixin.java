package com.liuyue.igny.mixins.rule.visibleSpectators;

import com.liuyue.igny.IGNYSettings;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
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
    public ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Shadow
    public abstract @NotNull Entity getCamera();

    @Inject(method = "updateInvisibilityStatus", at = @At("HEAD"), cancellable = true)
    private void noInvisibleSpectators(CallbackInfo ci) {
        if (IGNYSettings.VISIBLE_SPECTATORS.value()) {
            if (this.isSpectator() && this.getCamera() == this) {
                this.removeEffectParticles();
            } else {
                super.updateInvisibilityStatus();
            }
            ci.cancel();
        }
    }

    @Inject(method = "broadcastToPlayer", at = @At("HEAD"), cancellable = true)
    private void allowSpectatorsToBeSpectated(ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.VISIBLE_SPECTATORS.value()) {
            if (player.isSpectator()) {
                cir.setReturnValue(this.getCamera() == this);
            } else {
                cir.setReturnValue(super.broadcastToPlayer(player));
            }
        }
    }
}
