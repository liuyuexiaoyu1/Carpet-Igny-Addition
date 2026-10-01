package com.liuyue.igny.mixins.rule.noZombieReinforcement;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
//#if >= 1.21.11
//$$import net.minecraft.world.entity.monster.zombie.Zombie;
//#else
import net.minecraft.world.entity.monster.Zombie;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Zombie.class)
public class ZombieMixin {

    //#if >= 1.21.11
    /*$$@ModifyExpressionValue(
            method = "hurtServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/zombie/Zombie;getAttributeValue(Lnet/minecraft/core/Holder;)D")
    )
    private double igny$noZombieReinforcement(double original) {
        return IGNYSettings.NO_ZOMBIE_REINFORCEMENT.value() ? 0.0 : original;
    }$$*/
    //#else
    //#if >= 1.21.3
    /*$$@ModifyExpressionValue(
            method = "hurtServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Zombie;getAttributeValue(Lnet/minecraft/core/Holder;)D")
    )
    private double igny$noZombieReinforcement(double original) {
        return IGNYSettings.NO_ZOMBIE_REINFORCEMENT.value() ? 0.0 : original;
    }$$*/
    //#else
    //#if >= 1.20.6
    @ModifyExpressionValue(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Zombie;getAttributeValue(Lnet/minecraft/core/Holder;)D")
    )
    private double igny$noZombieReinforcement(double original) {
        return IGNYSettings.NO_ZOMBIE_REINFORCEMENT.value() ? 0.0 : original;
    }
    //#else
    /*$$@ModifyExpressionValue(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Zombie;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D")
    )
    private double igny$noZombieReinforcement(double original) {
        return IGNYSettings.NO_ZOMBIE_REINFORCEMENT.value() ? 0.0 : original;
    }$$*/
    //#endif
    //#endif
    //#endif
}
