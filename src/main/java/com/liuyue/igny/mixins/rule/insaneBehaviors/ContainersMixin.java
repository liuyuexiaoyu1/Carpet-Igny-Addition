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
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Containers;
//#if MC >= 26.2
//$$ import net.minecraft.world.entity.EntityTypes;
//#else
import net.minecraft.world.entity.EntityType;
//#endif
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

import static com.liuyue.igny.utils.insaneBehaviors.InsaneBehaviors.mapUnitVelocityToTriangularDistribution;
import static com.liuyue.igny.utils.insaneBehaviors.InsaneBehaviors.nextEvenlyDistributedPoint;

@Mixin(Containers.class)
public class ContainersMixin {

    @WrapOperation(method = "dropItemStack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"
    ))
    private static void setDeltaMovement(ItemEntity itemEntity, double d, double e, double f, Operation<Void> original, Level level, double originX, double originY, double originZ, ItemStack itemStack) {
        if (IGNYSettings.INSANE_BEHAVIORS.value().equals("off") || IGNYSettings.INSANE_BEHAVIORS_CART_YEETING_EXCEPTION.value().equals("disableContainerContents")) {
            original.call(itemEntity, d, e, f);
            return;
        }
        ArrayList<Float> unitList = nextEvenlyDistributedPoint(6);

        //#if MC >= 26.2
        //$$ double g = EntityTypes.ITEM.getWidth();
        //#else
        double g = EntityType.ITEM.getWidth();
        //#endif
        double h = 1.0 - g;
        double i = g / 2.0;
        double j = Math.floor(originX) + unitList.get(3) * h + i;
        double k = Math.floor(originY) + unitList.get(4) * h;
        double l = Math.floor(originZ) + unitList.get(5) * h + i;


        itemEntity.setPos(j, k, l);
        Vec3 unitVelocity = new Vec3(unitList.get(0), unitList.get(1), unitList.get(2));

        Vec3 velocity = switch (IGNYSettings.INSANE_BEHAVIORS.value()) {
            case "sensible" -> mapUnitVelocityToTriangularDistribution(
                    unitVelocity,
                    1,
                    0.0, 0.11485000171139836,
                    0.2, 0.11485000171139836,
                    0.0, 0.11485000171139836
            );
            case "extreme" -> mapUnitVelocityToTriangularDistribution(
                    unitVelocity,
                    8,
                    0.0, 0.05f,
                    0.2, 0.05f,
                    0.0, 0.05f
            );
            default -> throw new IllegalStateException("Unexpected insaneBehaviors value: " + IGNYSettings.INSANE_BEHAVIORS.value());
        };
        original.call(itemEntity, velocity.x, velocity.y, velocity.z);
    }
}