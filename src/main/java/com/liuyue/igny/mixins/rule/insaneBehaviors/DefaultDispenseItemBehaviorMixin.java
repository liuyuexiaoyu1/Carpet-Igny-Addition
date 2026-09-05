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
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

import static com.liuyue.igny.utils.insaneBehaviors.InsaneBehaviors.*;

@Mixin(DefaultDispenseItemBehavior.class)
public class DefaultDispenseItemBehaviorMixin {

	@WrapOperation(method = "spawnItem", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"
	))
	private static void setDeltaMovement(
			ItemEntity itemEntity,
			double d, double e, double f,
			Operation<ItemEntity> original,
			Level _level, ItemStack _itemStack, int i, Direction direction, Position _position
	) {
		if (IGNYSettings.INSANE_BEHAVIORS.value().equals("off")) {
			original.call(itemEntity, d, e, f);
			return;
		}
		ArrayList<Float> unitList = nextEvenlyDistributedPoint(3);
		Vec3 unitVelocity = new Vec3(unitList.get(0), unitList.get(2), unitList.get(1));

		double gx = direction.getStepX() == -1
				? 0.1 * (1-unitVelocity.x) + 0.2
				: 0.1 * unitVelocity.x + 0.2;

		double gz = direction.getStepZ() == -1
				? 0.1 * (1-unitVelocity.z) + 0.2
				: 0.1 * unitVelocity.z + 0.2;

		Vec3 velocity = switch (IGNYSettings.INSANE_BEHAVIORS.value()) {
			case "sensible" -> mapUnitVelocityToTriangularDistribution(
					unitVelocity,
					1,
					gx * (double) direction.getStepX(), 0.0172275 * (double) i,
					0.2,                                0.0172275 * (double) i,
					gz * (double) direction.getStepZ(), 0.0172275 * (double) i
			);
			case "extreme" -> mapUnitVelocityToTriangularDistribution(
					unitVelocity,
					8,
					gx * (double) direction.getStepX(), 0.0075 * (double) i,
					0.2,                                0.0075 * (double) i,
					gz * (double) direction.getStepZ(), 0.0075 * (double) i
			);
			default -> throw new IllegalStateException("Unexpected insaneBehaviors value: " + IGNYSettings.INSANE_BEHAVIORS.value());
		};
		original.call(itemEntity, velocity.x, velocity.y, velocity.z);
	}
}