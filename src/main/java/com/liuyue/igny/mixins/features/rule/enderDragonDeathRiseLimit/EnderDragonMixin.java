package com.liuyue.igny.mixins.features.rule.enderDragonDeathRiseLimit;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderDragon.class)
public class EnderDragonMixin {
    @WrapOperation(method = "tickDeath",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"))
    private void tickDeath(EnderDragon instance, MoverType moverType, Vec3 vec3, Operation<Void> original) {
        EnderDragon self = (EnderDragon) (Object) this;
        if (self.getY() >= IGNYSettings.enderDragonDeathRiseLimit && IGNYSettings.enderDragonDeathRiseLimit != -1145) {
            return;
        }
        original.call(instance, moverType, vec3);
    }
}