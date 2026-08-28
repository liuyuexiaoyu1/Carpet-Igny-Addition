package com.liuyue.igny.mixins.rule.generateNetherPortal;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FireChargeItem.class)
public abstract class FireChargeItemMixin {
    @Unique boolean flag;
    @Shadow
    protected abstract void playSound(Level level, BlockPos blockPos);

    @WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseFireBlock;canBePlacedAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
    private boolean onUseOn(Level level, BlockPos pos, Direction forwardDirection, Operation<Boolean> original, @Local(argsOnly = true) UseOnContext context) {
        boolean result = original.call(level, pos, forwardDirection);
        if (!result) {
            pos = context.getClickedPos();
            if (!IGNYSettings.GENERATE_NETHER_PORTAL.value()) {
                return false;
            }
            BlockState clickedState = level.getBlockState(pos);
            Player player = context.getPlayer();
            if (!clickedState.is(Blocks.OBSIDIAN) && !clickedState.is(Blocks.NETHER_PORTAL)) {
                return false;
            }
            if (player == null) {
                return false;
            }
            if (player.isShiftKeyDown()) {
                return false;
            }
            if (!BaseFireBlockInvoker.inPortalDimension(level) && !player.isCreative()) {
                return false;
            }
            Direction face = context.getClickedFace();
            BlockPos targetPos = pos.relative(face);
            if (!level.isEmptyBlock(targetPos) && !level.getBlockState(targetPos).is(Blocks.FIRE)) {
                return false;
            }
            BlockState targetState = level.getBlockState(targetPos);
            if (!targetState.is(Blocks.FIRE) && !targetState.is(Blocks.AIR)) {
                return false;
            }
            Direction.Axis portalAxis = getPortalAxisFromFace(face, player, clickedState);
            if (clickedState.is(Blocks.NETHER_PORTAL)) {
                Direction.Axis existingAxis = clickedState.getValue(NetherPortalBlock.AXIS);
                if ((existingAxis == Direction.Axis.Z && portalAxis == Direction.Axis.X) ||
                        (existingAxis == Direction.Axis.X && portalAxis == Direction.Axis.Z)) {
                    return false;
                }
            }

            BlockState portalState = Blocks.NETHER_PORTAL.defaultBlockState()
                    .setValue(NetherPortalBlock.AXIS, portalAxis);

            level.setBlock(targetPos, portalState, 2);
            this.playSound(level, targetPos);

            flag = true;
            return false;
        }
        return true;
    }

    @ModifyVariable(method = "useOn", at = @At(value = "STORE"))
    private boolean modifyFlag(boolean value) {
        if (flag) {
            flag = false;
            return true;
        }
        return value;
    }

    @Unique
    private static Direction.Axis getPortalAxisFromFace(Direction face, Player player, BlockState clickedState) {
        if (clickedState.is(Blocks.NETHER_PORTAL)) {
            if (face == Direction.UP || face == Direction.DOWN) {
                return clickedState.getValue(NetherPortalBlock.AXIS);
            } else {
                return face.getAxis();
            }
        }
        if (face != Direction.UP && face != Direction.DOWN) {
            return face.getAxis();
        }
        if (player != null) {
            Direction playerHorizontalFacing = player.getDirection();
            return playerHorizontalFacing.getAxis() == Direction.Axis.X ?
                    Direction.Axis.Z : Direction.Axis.X;
        }
        return Direction.Axis.X;
    }
}
