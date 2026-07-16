package com.liuyue.igny.mixins.rule.quickPlaceWater;

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

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!IGNYSettings.QUICK_PLACE_WATER.value()) return;
        if (!context.getHand().equals(InteractionHand.OFF_HAND)) return;

        Player player = context.getPlayer();
        if (player == null) return;

        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(Items.ICE)) return;

        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos placePos = clickedPos.relative(direction);
        BlockState clickedState = level.getBlockState(clickedPos);

        BlockPos targetPos;
        if (clickedState.getBlock() instanceof LiquidBlockContainer container && container.canPlaceLiquid(player, level, clickedPos, clickedState, Fluids.WATER)) {
            targetPos = clickedPos;
        } else {
            targetPos = placePos;
        }

        BlockState targetState = level.getBlockState(targetPos);
        boolean canPlaceInContainer = targetState.getBlock() instanceof LiquidBlockContainer lc && lc.canPlaceLiquid(player, level, targetPos, targetState, Fluids.WATER);
        boolean canReplace = targetState.canBeReplaced(Fluids.WATER);

        if (!targetState.isAir() && !canPlaceInContainer && !canReplace) {
            return;
        }

        if (level.isClientSide()) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        if (canPlaceInContainer) {
            ((LiquidBlockContainer) targetState.getBlock()).placeLiquid(level, targetPos, targetState, Fluids.WATER.defaultFluidState());
        } else {
            level.setBlock(targetPos, Fluids.WATER.defaultFluidState().createLegacyBlock(), 3);
        }

        if (!player.hasInfiniteMaterials()) {
            offhand.shrink(1);
        }

        level.playSound(null, targetPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PLACE, targetPos);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(Items.ICE));
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
