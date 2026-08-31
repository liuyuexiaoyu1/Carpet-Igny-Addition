package com.liuyue.igny.mixins.rule.generateNetherPortal;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlintAndSteelItem.class)
public class FlintAndSteelItemMixin {
    @Unique boolean flag;

    @WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseFireBlock;canBePlacedAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
    private boolean onUseOn(Level level, BlockPos pos, Direction forwardDirection, Operation<Boolean> original, @Local(argsOnly = true) UseOnContext context) {
        boolean result = original.call(level, pos, forwardDirection);
        if (!IGNYSettings.GENERATE_NETHER_PORTAL.value()) {
            return result;
        }
        BlockState clickedState = level.getBlockState(context.getClickedPos());
        if (!clickedState.is(Blocks.OBSIDIAN) && !clickedState.is(Blocks.NETHER_PORTAL)) {
            return result;
        }
        Player player = context.getPlayer();
        if (player == null) {
            return result;
        }
        if (player.isShiftKeyDown()) {
            return result;
        }
        if (!BaseFireBlockInvoker.inPortalDimension(level) && !player.isCreative()) {
            return result;
        }
        Direction face = context.getClickedFace();
        BlockPos targetPos = context.getClickedPos().relative(face);
        BlockState targetState = level.getBlockState(targetPos);
        if (!targetState.is(Blocks.FIRE) && !targetState.is(Blocks.AIR)) {
            return result;
        }
        Direction.Axis portalAxis = getPortalAxisFromFace(face, player, clickedState);
        if (clickedState.is(Blocks.NETHER_PORTAL)) {
            Direction.Axis existingAxis = clickedState.getValue(NetherPortalBlock.AXIS);
            if ((existingAxis == Direction.Axis.Z && portalAxis == Direction.Axis.X) ||
                    (existingAxis == Direction.Axis.X && portalAxis == Direction.Axis.Z)) {
                return result;
            }
        }

        BlockState portalState = Blocks.NETHER_PORTAL.defaultBlockState()
                .setValue(NetherPortalBlock.AXIS, portalAxis);

        level.setBlock(targetPos, portalState, 2);
        level.playSound(player, targetPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F,
                level.getRandom().nextFloat() * 0.4F + 0.8F);

        ItemStack itemStack = context.getItemInHand();
        itemStack.hurtAndBreak(1, player,
                //#if MC >= 12109
                //$$ context.getHand().asEquipmentSlot()
                //#elseif MC >= 12005
                LivingEntity.getSlotForHand(context.getHand())
                //#else
                //$$ p -> p.broadcastBreakEvent(context.getHand())
                //#endif
        );

        flag = true;
        return false;
    }

    @Inject(method = "useOn", at = @At(value = "RETURN"), cancellable = true)
    private void modifyFlag(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (flag) {
            flag = false;
            //#if MC >= 12102
            //$$ cir.setReturnValue(InteractionResult.SUCCESS);
            //#else
            cir.setReturnValue(InteractionResult.sidedSuccess(context.getLevel().isClientSide()));
            //#endif
        }
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