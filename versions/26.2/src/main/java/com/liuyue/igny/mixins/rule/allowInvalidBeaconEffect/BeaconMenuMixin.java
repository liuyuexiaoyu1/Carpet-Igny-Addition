package com.liuyue.igny.mixins.rule.allowInvalidBeaconEffect;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.BeaconMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(BeaconMenu.class)
public class BeaconMenuMixin {
    @Inject(method = "updateEffects", at = @At(value = "RETURN"), cancellable = true)
    private void updateEffects(Optional<Holder<MobEffect>> primary, Optional<Holder<MobEffect>> secondary, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.ALLOW_INVALID_BEACON_EFFECT.value()) {
            cir.setReturnValue(true);
        }
    }
}
