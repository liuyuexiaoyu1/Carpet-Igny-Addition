package com.liuyue.igny.mixins.rule.lightningBoltNoFire;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract DamageSources damageSources();

    @Inject(method = "thunderHit", at = @At(value = "HEAD"), cancellable = true)
    private void onThunderHit(ServerLevel serverLevel, LightningBolt lightningBolt, CallbackInfo ci) {
        if (IGNYSettings.LIGHTNING_BOLT_NO_FIRE.value()) {
            //#if MC >= 12102
            //$$ ((Entity) (Object) this).hurtServer(serverLevel, this.damageSources().lightningBolt(), 5.0F);
            //#else
            ((Entity) (Object) this).hurt(this.damageSources().lightningBolt(), 5.0F);
            //#endif
            ci.cancel();
        }
    }
}
