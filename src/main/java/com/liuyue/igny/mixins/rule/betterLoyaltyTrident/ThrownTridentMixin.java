package com.liuyue.igny.mixins.rule.betterLoyaltyTrident;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public class ThrownTridentMixin {
    @Shadow
    private boolean dealtDamage;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownTrident;isNoPhysics()Z"))
    private void tick(CallbackInfo ci, @Local Entity owner, @Local int i) {
        if (IGNYSettings.betterLoyaltyTrident) {
            ThrownTrident trident = (ThrownTrident) (Object) this;
            MinecraftServer server = trident.level().getServer();
            if (owner != null && i > 0 && server != null) {
                double dx = trident.getX() - owner.getX();
                double dz = trident.getZ() - owner.getZ();
                double distanceXZSq = dx * dx + dz * dz;
                int simDistanceBlocks = server.getPlayerList().getSimulationDistance() * 16;
                double threshold = Math.max(16.0, simDistanceBlocks - 1.0);
                double thresholdSq = threshold * threshold;
                Level level = trident.level();
                boolean isTridentInVoid = trident.getY() < level.getMinBuildHeight() && !(owner.getY() < level.getMinBuildHeight());
                boolean isTridentTooHigh = trident.getY() > level.getMaxBuildHeight() && !(owner.getY() > level.getMaxBuildHeight());
                if ((distanceXZSq > thresholdSq || isTridentInVoid || isTridentTooHigh) && !trident.isNoPhysics()) {
                    this.dealtDamage = true;
                }
            }
        }
    }
}
