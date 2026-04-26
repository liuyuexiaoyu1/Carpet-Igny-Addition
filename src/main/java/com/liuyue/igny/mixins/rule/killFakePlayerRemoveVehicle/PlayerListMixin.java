package com.liuyue.igny.mixins.rule.killFakePlayerRemoveVehicle;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.utils.RuleUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Shadow
    @Final
    private List<ServerPlayer> players;

    @Inject(method = "removeAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"))
    private void removeFakePlayerRemoveVehicle(CallbackInfo ci, @Local int i) {
        ServerPlayer serverPlayer = this.players.get(i);
        if (serverPlayer instanceof EntityPlayerMPFake) {
            RuleUtil.removeVehicle(serverPlayer);
        }
    }
}
