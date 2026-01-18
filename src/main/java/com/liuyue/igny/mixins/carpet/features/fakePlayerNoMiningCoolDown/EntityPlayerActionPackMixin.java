package com.liuyue.igny.mixins.carpet.features.fakePlayerNoMiningCoolDown;

import carpet.patches.EntityPlayerMPFake;
import carpet.helpers.EntityPlayerActionPack;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerActionPack.class)
public abstract class EntityPlayerActionPackMixin {

    @Shadow private int blockHitDelay;

    @Shadow
    @Final
    private ServerPlayer player;

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void disableMiningCooldown(CallbackInfo ci) {
        if (this.player instanceof EntityPlayerMPFake && IGNYSettings.fakePlayerNoBreakingCoolDown) {
            this.blockHitDelay = 0;
        }
    }
}


