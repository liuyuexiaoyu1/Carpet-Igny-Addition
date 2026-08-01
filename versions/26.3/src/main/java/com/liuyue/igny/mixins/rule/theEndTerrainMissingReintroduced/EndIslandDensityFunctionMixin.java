package com.liuyue.igny.mixins.rule.theEndTerrainMissingReintroduced;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net/minecraft/world/level/levelgen/DensityFunctions$EndIslandDensityFunction")
public class EndIslandDensityFunctionMixin {
    @ModifyVariable(method = "getHeightValue", at = @At(value = "RETURN"), name = "doffs")
    private static float modifyDoffs(float value, @Local(ordinal = 0, argsOnly = true) int sectionX, @Local(ordinal = 1, argsOnly = true) int sectionZ) {
        if (IGNYSettings.THE_END_TERRAIN_MISSING_REINTRODUCE.value()) {
            float doffs = 100.0F - Mth.sqrt(sectionX * sectionX + sectionZ * sectionZ) * 8.0F;
            return Mth.clamp(doffs, -100.0F, 80.0F);
        }
        return value;
    }
}
