package com.liuyue.igny.mixins.rule.survivalFlyNoClip;

import com.liuyue.igny.helper.NoClipHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BlockItem.class, priority = 1100)
public class BlockItemMixin {
    @WrapOperation(method = "canPlace", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;isUnobstructed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Z"
    ))
    private boolean isUnobstructed(Level instance, BlockState state, BlockPos pos, CollisionContext context,
                                   Operation<Boolean> original,
                                   @Local(argsOnly = true) BlockPlaceContext blockPlaceContext) {
        Player player = blockPlaceContext.getPlayer();
        if (NoClipHelper.isActiveFlyingPlayer(player)) {
            VoxelShape shape = state.getCollisionShape(instance, pos, context);
            return shape.isEmpty() || instance.isUnobstructed(player, shape.move(pos.getX(), pos.getY(), pos.getZ()));
        }
        return original.call(instance, state, pos, context);
    }
}
