package com.liuyue.igny.mixins.rule.trialSpawnerCoolDown;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrialSpawnerData.class)
public class TrialSpawnerDataMixin {
    @Inject(method = "isCooldownFinished", at = @At("HEAD"), cancellable = true)
    private void isCooldownFinished(ServerLevel serverLevel, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.trialSpawnerCoolDown == 0) {
            cir.setReturnValue(true);
        }
    }
}
