package com.liuyue.igny.mixins.rule.killFakePlayerRemoveVehicle;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(EntityPlayerMPFake.class)
public class EntityPlayerMPFakeMixin {
    @Inject(method = "kill(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"))
    private void kill(Component reason, CallbackInfo ci) {
        if (!Objects.equals(IGNYSettings.killFakePlayerRemoveVehicle, "true")) {
            EntityPlayerMPFake fakePlayer = (EntityPlayerMPFake) (Object) this;
            boolean shouldKeep = true;
            if (Objects.equals(IGNYSettings.killFakePlayerRemoveVehicle, "canBoatTrade")) {
                if (fakePlayer.getVehicle() != null){
                    shouldKeep = fakePlayer.getVehicle().getPassengers().stream().noneMatch(entity -> entity instanceof Villager || entity instanceof WanderingTrader);
                }
            }
            if (shouldKeep) {
                fakePlayer.stopRiding();
            }
        }
    }
}
