package com.liuyue.igny.mixins.rule.generateNetherPortal;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireChargeItem.class)
public abstract class FireChargeItemMixin {
    @Shadow
    protected abstract void playSound(Level level, BlockPos blockPos);

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void onUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!IGNYSettings.generateNetherPortal) {
            return;
        }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(pos);
        Player player = context.getPlayer();
        if (!clickedState.is(Blocks.OBSIDIAN) && !clickedState.is(Blocks.NETHER_PORTAL)) {
            return;
        }
        if (!isInAllowedDimension(player, level)) {
            return;
        }
        if (player.isShiftKeyDown()){
            return;
        }
        Direction face = context.getClickedFace();
        BlockPos targetPos = pos.relative(face);
        if (!level.isEmptyBlock(targetPos) && !level.getBlockState(targetPos).is(Blocks.FIRE)) {
            return;
        }
        if (BaseFireBlockInvoker.isPortal(level,targetPos,face)) {
            return;
        }
        BlockState targetState = level.getBlockState(targetPos);
        if (!targetState.is(Blocks.FIRE) && !targetState.is(Blocks.AIR)) {
            return;
        }
        Direction.Axis portalAxis = getPortalAxisFromFace(face, player, clickedState);
        if (clickedState.is(Blocks.NETHER_PORTAL)) {
            Direction.Axis existingAxis = clickedState.getValue(NetherPortalBlock.AXIS);
            if ((existingAxis == Direction.Axis.Z && portalAxis == Direction.Axis.X) ||
                    (existingAxis == Direction.Axis.X && portalAxis == Direction.Axis.Z)) {
                return;
            }
        }

        BlockState portalState = Blocks.NETHER_PORTAL.defaultBlockState()
                .setValue(NetherPortalBlock.AXIS, portalAxis);

        level.setBlock(targetPos, portalState, 2);
        this.playSound(level,targetPos);

        //#if MC >= 12102
        //$$ cir.setReturnValue(InteractionResult.SUCCESS);
        //#else
        cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide()));
        //#endif
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

    @Unique
    private static boolean isInAllowedDimension(Player player, Level level) {
        if (player == null) {
            return false;
        }
        if (player.isCreative()) {
            return true;
        }
        return level.dimension() == Level.OVERWORLD || level.dimension() == Level.NETHER;
    }
}
