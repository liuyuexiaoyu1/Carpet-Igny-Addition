package com.liuyue.igny.mixins.rule.killFakePlayerRemoveVehicle;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.utils.RuleUtil;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMPFake.class)
public class EntityPlayerMPFakeMixin {
    @Inject(method = "kill(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"))
    private void kill(Component reason, CallbackInfo ci) {
        EntityPlayerMPFake fakePlayer = (EntityPlayerMPFake) (Object) this;
        RuleUtil.removeVehicle(fakePlayer);
    }
}
