package com.liuyue.igny.mixins.rule.locatorBarNoFakePlayer;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.waypoints.ServerWaypointManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.liuyue.igny.IGNYSettings;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWaypointManager.class)
public abstract class ServerWaypointManagerMixin {
    @Inject(method = "isLocatorBarEnabledFor", at = @At("HEAD"), cancellable = true)
    private static void isLocatorBarEnabledFor(ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.LOCATOR_BAR_NO_FAKE_PLAYER.value() && player instanceof EntityPlayerMPFake) cir.setReturnValue(false);
    }

    @Inject(method = "addPlayer", at = @At(value = "HEAD"), cancellable = true)
    private void addPlayer(ServerPlayer player, CallbackInfo ci) {
        if (IGNYSettings.LOCATOR_BAR_NO_FAKE_PLAYER.value() && player instanceof EntityPlayerMPFake) ci.cancel();
    }
}

