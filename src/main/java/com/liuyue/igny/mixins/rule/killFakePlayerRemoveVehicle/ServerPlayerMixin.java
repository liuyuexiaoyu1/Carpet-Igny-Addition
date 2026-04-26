package com.liuyue.igny.mixins.rule.killFakePlayerRemoveVehicle;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.utils.RuleUtil;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "disconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;ejectPassengers()V"))
    private void disconnect(CallbackInfo ci) {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
        if (serverPlayer instanceof EntityPlayerMPFake) {
            RuleUtil.removeVehicle(serverPlayer);
        }
    }
}
