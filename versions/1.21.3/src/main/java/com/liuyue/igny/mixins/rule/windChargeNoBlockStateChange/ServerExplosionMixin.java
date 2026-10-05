package com.liuyue.igny.mixins.rule.windChargeNoBlockStateChange;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(ServerExplosion.class)
public class ServerExplosionMixin {

    @Final
    @Shadow
    private Explosion.BlockInteraction blockInteraction;

    @Inject(method = "shouldAffectBlocklikeEntities", at = @At(value = "HEAD"), cancellable = true)
    private void shouldAffectBlocklikeEntities(CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.WIND_CHARGE_NO_BLOCK_STATE_CHANGE.value()
                && this.blockInteraction == Explosion.BlockInteraction.TRIGGER_BLOCK) {
            cir.setReturnValue(false);
        }
    }
}
