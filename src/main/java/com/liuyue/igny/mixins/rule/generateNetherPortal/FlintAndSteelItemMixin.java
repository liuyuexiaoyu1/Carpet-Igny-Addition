package com.liuyue.igny.mixins.rule.generateNetherPortal;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
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
import java.util.Objects;

@Mixin(FlintAndSteelItem.class)
public class FlintAndSteelItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void onUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if(!Objects.equals(IGNYSettings.generateNetherPortal, "false")) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState clickedState = level.getBlockState(pos);
            Player player = context.getPlayer();
            if (!clickedState.is(Blocks.OBSIDIAN)||!clickedState.is(Blocks.NETHER_PORTAL)) {
                return;
            }
            Direction face = context.getClickedFace();
            BlockPos targetPos = pos.relative(face);
            boolean isPlayerSneaking = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
            boolean cancel = false;
            if (isPlayerSneaking && isInPortalDimension(player,level)) {
                if (!level.isClientSide()) {
                    if (level.isEmptyBlock(targetPos)) {
                        Direction.Axis portalAxis = getPortalAxisFromFace(face,player);
                        BlockState portalState = Blocks.NETHER_PORTAL.defaultBlockState()
                                .setValue(NetherPortalBlock.AXIS, portalAxis);
                        if (level.getBlockState(targetPos).is(Blocks.FIRE) || level.getBlockState(targetPos).is(Blocks.AIR)) {
                            level.setBlock(targetPos, portalState, 2);
                            level.playSound(player, targetPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                            context.getItemInHand().hurtAndBreak(1, player,
                                    //#if MC >= 12109
                                    //$$ context.getHand().asEquipmentSlot()
                                    //#else
                                    LivingEntity.getSlotForHand(context.getHand())
                                    //#endif
                            );
                            cancel = true;
                        }
                    }
                }
                if (cancel) {
                    //#if MC >= 12102
                    //$$ cir.setReturnValue(InteractionResult.SUCCESS);
                    //#else
                    cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide()));
                    //#endif
                }
            }
        }
    }

    @Unique
    private static Direction.Axis getPortalAxisFromFace(Direction face, Player player) {
        if (face != Direction.UP && face != Direction.DOWN) {
            return face.getAxis();
        }
        if (player != null) {
            Direction playerHorizontalFacing = player.getDirection();

            if (playerHorizontalFacing.getAxis() == Direction.Axis.X) {
                return Direction.Axis.Z;
            }
            else if (playerHorizontalFacing.getAxis() == Direction.Axis.Z) {
                return Direction.Axis.X;
            }
        }

        return Direction.Axis.X;
    }

    @Unique
    private static boolean isInPortalDimension(Player player, Level level) {
        if (Objects.equals(IGNYSettings.generateNetherPortal, "creative") && player.isCreative()) {return true;}
        return level.dimension() == Level.OVERWORLD || level.dimension() == Level.NETHER;
    }
}