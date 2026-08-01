package com.liuyue.igny.mixins.rule.airCompost;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
//#if MC >= 12102
//$$ import net.minecraft.world.InteractionResult;
//#else
import net.minecraft.world.ItemInteractionResult;
//#endif
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin extends Block {
    public ComposterBlockMixin(Properties properties) {
        super(properties);
    }

    //#if MC <= 12004
    //$$ @Inject(method = "use", at = @At(value = "HEAD"), cancellable = true)
    //#else
    @Inject(method = "useItemOn", at = @At(value = "HEAD"), cancellable = true)
    //#endif
    //#if MC >= 12102
    //$$ private void useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir)
    //#elseif MC >= 12101
    private void useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<ItemInteractionResult> cir)
    //#else
    //$$ private void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir)
    //#endif
    {
        if (IGNYSettings.AIR_COMPOST.value()) {
            int i = state.getValue(ComposterBlock.LEVEL);
            //#if MC >= 12101
            if (i < 8 && stack.is(Items.AIR))
            //#else
                //$$ if (i < 8 && player.getItemInHand(hand).is(Items.AIR))
                //#endif
            {
                if (i < 7 && !level.isClientSide()) {
                    int j = i + 1;
                    BlockState blockState = state.setValue(ComposterBlock.LEVEL, j);
                    level.setBlock(pos, blockState, 3);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockState));
                    if (j == 7) {
                        level.scheduleTick(pos, state.getBlock(), 20);
                    }
                    level.levelEvent(1500, pos, state != blockState ? 1 : 0);
                }
                //#if MC >= 12102
                //$$ cir.setReturnValue(InteractionResult.SUCCESS);
                //#elseif MC >= 12101
                cir.setReturnValue(ItemInteractionResult.sidedSuccess(level.isClientSide()));
                //#else
                //$$ cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide()));
                //#endif
            }
        }
    }
}
