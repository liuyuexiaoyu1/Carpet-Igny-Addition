/*
 * This file is part of the JoaCarpet project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Joa and contributors
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.entity.vehicle.VehicleEntity; //#replace < 1.20.3 ? import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.At;
//?>= 1.21.3 ? import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;

@Mixin(VehicleEntity.class) //#replace < 1.20.3 ? @Mixin(AbstractMinecart.class)
public class VehicleEntityMixin {
    //#if >= 1.21.3
    /*$$@WrapOperation(
            method = "destroy(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/Item;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
            )
    )$$*/
    //#elseif >= 1.20.3
    @WrapOperation(
            method = "destroy(Lnet/minecraft/world/item/Item;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
            )
    )
    //#else
    /*$$@WrapOperation(
            method = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;destroy(Lnet/minecraft/world/damagesource/DamageSource;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
    )
    )$$*/
    //#endif
    private ItemEntity spawnAtLocation(
            VehicleEntity instance, //#replace < 1.20.3 ? AbstractMinecart instance,
            //?>= 1.21.3 ? ServerLevel world,
            ItemStack itemStack, Operation<ItemEntity> original) {
        if (IGNYSettings.INSANE_BEHAVIORS.value().equals("off") || IGNYSettings.INSANE_BEHAVIORS_CART_YEETING_EXCEPTION.value().equals("disableVehicleItem")) {
            return original.call(instance, itemStack); //#replace >= 1.21.3 ? return original.call(instance, world, itemStack);
        }
        ArrayList<Float> unitValueList = InsaneBehaviors.nextEvenlyDistributedPoint(2);
        Vec3 velocity = new Vec3(
                unitValueList.get(0) * 0.2 - 0.1,
                0.2,
                unitValueList.get(1) * 0.2 - 0.1
        );
        Level level = instance.level();
        ItemEntity itemEntity = new ItemEntity(level, instance.getX(), instance.getY(), instance.getZ(), itemStack, velocity.x, velocity.y, velocity.z);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
        return itemEntity;
    }
}
