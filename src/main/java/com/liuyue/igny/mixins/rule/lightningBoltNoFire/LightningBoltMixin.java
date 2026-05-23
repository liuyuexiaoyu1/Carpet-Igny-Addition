package com.liuyue.igny.mixins.rule.lightningBoltNoFire;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public class LightningBoltMixin {
    @Inject(method = "spawnFire", at = @At("HEAD"), cancellable = true)
    private void spawnFire(int i, CallbackInfo ci) {
        if (IGNYSettings.LIGHTNING_BOLT_NO_FIRE.value()) ci.cancel();
    }
}
