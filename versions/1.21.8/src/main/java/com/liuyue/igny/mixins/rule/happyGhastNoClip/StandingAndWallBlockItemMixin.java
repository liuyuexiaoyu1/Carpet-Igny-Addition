package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StandingAndWallBlockItem.class)
public class StandingAndWallBlockItemMixin
{
    @WrapOperation(method = "getPlacementState", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelReader;isUnobstructed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Z"
    ))
    private boolean canPlayerPlace(
            LevelReader instance, BlockState blockState, BlockPos blockPos, CollisionContext collisionContext, Operation<Boolean> original,@Local(argsOnly = true) BlockPlaceContext blockPlaceContext
    )
    {
        Player player = blockPlaceContext.getPlayer();
        if (player != null && player.getRootVehicle() instanceof HappyGhast && IGNYSettings.happyGhastNoClip) {
            VoxelShape voxelShape = blockState.getCollisionShape(instance, blockPos, collisionContext);
            return voxelShape.isEmpty() || instance.isUnobstructed(player, voxelShape.move(blockPos.getX(), blockPos.getY(), blockPos.getZ()));

        }
        return original.call(instance, blockState, blockPos, collisionContext);
    }


}

