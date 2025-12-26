package com.liuyue.igny.mixins.features.rule.enderDragonDeathDropExp;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnderDragon.class)
public class EnderDragonMixin {
    @ModifyVariable(method = "tickDeath", at = @At(value = "STORE"))
    private int dropExp(int original) {
        if (IGNYSettings.enderDragonDeathDropExp != -1) {
            return IGNYSettings.enderDragonDeathDropExp;
        }
        return original;
    }
}
