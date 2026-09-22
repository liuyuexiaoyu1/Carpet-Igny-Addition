package com.liuyue.igny.mixins.rule.quickPlaceLava;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?>= 26.3 ? import net.minecraft.world.item.component.SwingAnimation;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!IGNYSettings.QUICK_PLACE_LAVA.value()) return;
        if (!context.getHand().equals(InteractionHand.OFF_HAND)) return;

        Player player = context.getPlayer();
        if (player == null) return;

        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(Items.MAGMA_BLOCK)) return;

        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos placePos = clickedPos.relative(direction);
        BlockState clickedState = level.getBlockState(clickedPos);

        BlockPos targetPos;
        if (clickedState.getBlock() instanceof LiquidBlockContainer container && container.canPlaceLiquid(player, level, clickedPos, clickedState, Fluids.LAVA)) //#replace <= 1.20.1 ? if (clickedState.getBlock() instanceof LiquidBlockContainer container && container.canPlaceLiquid(level, clickedPos, clickedState, Fluids.LAVA))
        {
            targetPos = clickedPos;
        } else {
            targetPos = placePos;
        }

        BlockState targetState = level.getBlockState(targetPos);
        boolean canPlaceInContainer = targetState.getBlock() instanceof LiquidBlockContainer lc && lc.canPlaceLiquid(player, level, targetPos, targetState, Fluids.LAVA); //#replace <= 1.20.1 ? boolean canPlaceInContainer = targetState.getBlock() instanceof LiquidBlockContainer lc && lc.canPlaceLiquid(level, targetPos, targetState, Fluids.LAVA);
        boolean canReplace = targetState.canBeReplaced(Fluids.LAVA);

        if (!targetState.isAir() && !canPlaceInContainer && !canReplace) {
            return;
        }

        if (level.isClientSide()) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        if (canPlaceInContainer) {
            ((LiquidBlockContainer) targetState.getBlock()).placeLiquid(level, targetPos, targetState, Fluids.LAVA.defaultFluidState());
        } else {
            level.setBlock(targetPos, Fluids.LAVA.defaultFluidState().createLegacyBlock(), 3);
        }

        if (!player.hasInfiniteMaterials()) //#replace <= 1.20.5 ? if (!player.getAbilities().instabuild)
        {
            offhand.shrink(1);
        }

        level.playSound(null, targetPos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PLACE, targetPos);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(Items.MAGMA_BLOCK));
        }
        player.swing(InteractionHand.OFF_HAND); //#replace >= 26.3 ? player.swing(InteractionHand.OFF_HAND, SwingAnimation.DEFAULT, true);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
