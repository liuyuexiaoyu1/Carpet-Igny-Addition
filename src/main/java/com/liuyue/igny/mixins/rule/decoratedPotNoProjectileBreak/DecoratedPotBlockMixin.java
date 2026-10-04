package com.liuyue.igny.mixins.rule.decoratedPotNoProjectileBreak;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.block.DecoratedPotBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DecoratedPotBlock.class)
public class DecoratedPotBlockMixin {
    @Inject(method = "onProjectileHit", at = @At(value = "HEAD"), cancellable = true)
    private void onProjectileHit(CallbackInfo ci) {
        if (IGNYSettings.DECORATED_POT_PREJECTILE_BREAK.value()) {
            ci.cancel();
        }
    }
}
