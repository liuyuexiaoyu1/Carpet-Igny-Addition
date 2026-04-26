package com.liuyue.igny.mixins.rule.killFakePlayerRemoveVehicle;
import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.utils.RuleUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.server.IntegratedServer;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
    @Inject(method = "method_4816", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;remove(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void removeFakePlayer(CallbackInfo ci, @Local ServerPlayer serverPlayer) {
        if (serverPlayer instanceof EntityPlayerMPFake) {
            RuleUtil.removeVehicle(serverPlayer);
        }
    }
}
