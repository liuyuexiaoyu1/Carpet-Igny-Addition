package com.liuyue.igny.mixins.rule.noZombieReinforcement;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Zombie.class)
public class ZombieMixin {
    @ModifyExpressionValue(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Zombie;getAttributeValue(Lnet/minecraft/core/Holder;)D")
    )
    private double igny$noZombieReinforcement(double original) {
        return IGNYSettings.NO_ZOMBIE_REINFORCEMENT.value() ? 0.0 : original;
    }

}
