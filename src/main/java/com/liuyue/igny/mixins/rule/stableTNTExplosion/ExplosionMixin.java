package com.liuyue.igny.mixins.rule.stableTNTExplosion;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//#if MC >= 12102
//$$ import net.minecraft.world.level.ServerExplosion;
//#else
import net.minecraft.world.level.Explosion;
//#endif

//#if MC >= 12102
//$$ @Mixin(ServerExplosion.class)
//#else
@Mixin(Explosion.class)
//#endif
public class ExplosionMixin {
    @ModifyExpressionValue(
            //#if MC >= 12102
            //$$ method = "calculateExplodedPositions",
            //#else
            method = "explode",
            //#endif
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextFloat()F")
    )
    private float stableRayStrength(float original) {
        return switch (IGNYSettings.stableTNTExplosion) {
            case "minimum" -> 0.0F;
            case "average" -> 0.5F;
            case "maximum" -> 1.0F;
            default -> original;
        };
    }
}
