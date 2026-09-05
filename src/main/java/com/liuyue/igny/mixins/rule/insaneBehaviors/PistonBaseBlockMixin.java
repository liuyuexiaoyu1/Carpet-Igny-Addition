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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

import static com.liuyue.igny.utils.insaneBehaviors.InsaneBehaviors.nextEvenlyDistributedPoint;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {
    @WrapOperation(method = "moveBlocks", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V"
    ))
    private void dropResources(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, @Nullable BlockEntity blockEntity, Operation<Void> original) {
        if (IGNYSettings.INSANE_BEHAVIORS.value().equals("off")) {
            original.call(blockState, levelAccessor, blockPos, blockEntity);
            return;
        }
        if (levelAccessor instanceof ServerLevel) {
            Level level = (ServerLevel) levelAccessor;
            Block.getDrops(blockState, (ServerLevel)levelAccessor, blockPos, blockEntity).forEach(itemStack -> {
                customPopResource(level, blockPos, itemStack);
            });
            blockState.spawnAfterBreak((ServerLevel)levelAccessor, blockPos, ItemStack.EMPTY, true);
        }
    }

    @Unique
    private static void customPopResource(Level level, BlockPos blockPos, ItemStack itemStack) {
        ArrayList<Float> unitValueList = nextEvenlyDistributedPoint(5);
        double velX = unitValueList.get(0) * 0.2 - 0.1;
        double velZ = unitValueList.get(1) * 0.2 - 0.1;
        double posX = (double)blockPos.getX() + 0.5 + unitValueList.get(2) * 0.5 - 0.25;
        double posZ = (double)blockPos.getZ() + 0.5 + unitValueList.get(3) * 0.5 - 0.25;
        double posY = (double)blockPos.getY() + 0.5 + unitValueList.get(4) * 0.5 - 0.25;
        BlockMixin.popResourceInvoker(level, () -> new ItemEntity(level, posX, posY, posZ, itemStack, velX, 0.2, velZ), itemStack);
    }
}