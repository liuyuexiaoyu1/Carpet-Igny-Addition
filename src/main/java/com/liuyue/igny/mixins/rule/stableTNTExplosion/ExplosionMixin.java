package com.liuyue.igny.mixins.rule.stableTNTExplosion;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.world.level.Explosion; //#replace >= 1.21.2 ? import net.minecraft.world.level.ServerExplosion;

@Mixin(Explosion.class) //#replace >= 1.21.2 ? @Mixin(ServerExplosion.class)
public class ExplosionMixin {
    @Shadow
    @Final
    private Entity source;

    @ModifyExpressionValue(
            method = "explode", //#replace >= 1.21.2 ? method = "calculateExplodedPositions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextFloat()F")
    )
    private float stableRayStrength(float original) {
        if (!(this.source instanceof PrimedTnt)) {
            return original;
        }

        return switch (IGNYSettings.STABLE_TNT_EXPLOSION.value()) {
            case "minimum" -> 0.0F;
            case "average" -> 0.5F;
            case "maximum" -> 1.0F;
            default -> original;
        };
    }
}
