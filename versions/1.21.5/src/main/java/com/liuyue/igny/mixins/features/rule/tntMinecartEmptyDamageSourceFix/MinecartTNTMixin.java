package com.liuyue.igny.mixins.features.rule.tntMinecartEmptyDamageSourceFix;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartTNT.class)
public abstract class MinecartTNTMixin
        //#if MC < 12109
        extends AbstractMinecart
        //#endif
{
    //#if MC < 12109
    protected MinecartTNTMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow protected abstract void explode(@Nullable DamageSource damageSource, double d);

    @Shadow private ThreadLocal<DamageSource> ignitionSource;

    @Inject(method="causeFallDamage", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/MinecartTNT;explode(D)V"), cancellable = true)
    private void onCauseFallDamage(double d, float f, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.tntMinecartEmptyDamageSourceFix) {
            if (d >= 3.0) {
                double e = d / 10.0;
                this.explode(this.ignitionSource.get(), e * e);
            }
            cir.setReturnValue(super.causeFallDamage(d, f, damageSource));
        }
    }
    //#endif
}