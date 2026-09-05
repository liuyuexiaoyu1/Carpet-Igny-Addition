/*
 * This file is part of the JoaCarpet project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Joa and contributors
 *
 * Ported to Carpet IGNY Addition (com.liuyue.igny) under LGPL-3.0.
 * Only the insaneBehaviors rule family is ported; all credits for the
 * original logic belong to the JoaCarpet authors.
 */

package com.liuyue.igny.mixins.rule.insaneBehaviors;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.insaneBehaviors.InsaneBehaviors;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

import static com.liuyue.igny.utils.insaneBehaviors.InsaneBehaviors.mapUnitVelocityToTriangularDistribution;

@Mixin(Projectile.class)
public class ProjectileMixin {
    @WrapOperation(
           method = "getMovementToShoot",
            at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
    ))
    private Vec3 add(Vec3 vec3, double d, double e, double f, Operation<Vec3> original, double deltaMovementX, double deltaMovementY, double deltaMovementZ, float deltaMovementMultiplier, float divergence) {
        if (IGNYSettings.INSANE_BEHAVIORS.value().equals("off")) {
            return original.call(vec3, d, e, f);
        }

        ArrayList<Float> unitList = InsaneBehaviors.nextEvenlyDistributedPoint(3);
        Vec3 unitVelocity = new Vec3(unitList.get(0), unitList.get(2), unitList.get(1));
        Vec3 velocity = switch (IGNYSettings.INSANE_BEHAVIORS.value()) {
            case "sensible" -> mapUnitVelocityToTriangularDistribution(
                    unitVelocity,
                    1,
                    0.0, 0.0172275 * (double)divergence,
                    0.0, 0.0172275 * (double)divergence,
                    0.0, 0.0172275 * (double)divergence
            );
            case "extreme" -> mapUnitVelocityToTriangularDistribution(
                    unitVelocity,
                    8,
                    0.0, (double)0.0075F * (double)divergence,
                    0.0, (double)0.0075F * (double)divergence,
                    0.0, (double)0.0075F * (double)divergence
            );
            default -> throw new IllegalStateException("Unexpected insaneBehaviors value: " + IGNYSettings.INSANE_BEHAVIORS.value());
        };
        return original.call(vec3, vec3.x + velocity.x, vec3.y + velocity.y, vec3.z + velocity.z);
    }

}