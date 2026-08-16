package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.decodeProtocolValueFromHitDim;
import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.getRelativeHitX;
import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.isExtraProtocol;
import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.isProtocol;

@Mixin(value = BlockItem.class, priority = 950)
public abstract class BlockItemMixin {
    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract boolean canPlace(BlockPlaceContext context, BlockState state);

    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    private void igny_betterEasyPlaceProtocolDecode(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!BetterEasyPlaceProtocolHandler.isRuleEnabled()) return;
        double relativeHitX = getRelativeHitX(context.getClickLocation(), context.getClickedPos());
        if (!isProtocol(relativeHitX)) return;
        int protocolValue = decodeProtocolValueFromHitDim(relativeHitX);
        boolean isExtended = isExtraProtocol(protocolValue);
        BlockState state = BetterEasyPlaceProtocolHandler.decodePlacementState(this.getBlock(), context);
        if (state == null) {
            if (isExtended && BetterEasyPlaceProtocolHandler.getAdapter(this.getBlock()) != null) {
                cir.setReturnValue(null);
                cir.cancel();
            }
            return;
        }
        if (!this.canPlace(context, state)) return;
        cir.setReturnValue(state);
    }

    @Inject(method = "place", at = @At("HEAD"))
    private void igny_setPlaceState(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (BetterEasyPlaceProtocolHandler.isRuleEnabled() && isProtocol(getRelativeHitX(context.getClickLocation(), context.getClickedPos()))) {
            BetterEasyPlaceProtocolHandler.setEasyPlaceState(true);
        }
    }

    @Inject(method = "place", at = @At("RETURN"))
    private void igny_clearPlaceState(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        BetterEasyPlaceProtocolHandler.setEasyPlaceState(false);
        BetterEasyPlaceProtocolHandler.setPlaceProperty(0);
    }

    @WrapOperation(
            method = "place",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean igny_placePistonHead(BlockItem instance, BlockPlaceContext context, BlockState state, Operation<Boolean> original) {
        boolean result = original.call(instance, context, state);
        
        if (result && BetterEasyPlaceProtocolHandler.hasPlaceFlag(BetterEasyPlaceProtocolHandler.EASY_PLACE_PISTON_PLACE_HEAD)) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState placed = level.getBlockState(pos);
            if (placed.getBlock() instanceof PistonBaseBlock) {
                net.minecraft.core.Direction facing = placed.getValue(PistonBaseBlock.FACING);
                BlockPos headPos = pos.relative(facing);
                BlockState headTarget = level.getBlockState(headPos);
                if (headTarget.isAir() || headTarget.canBeReplaced()) {
                    BlockState headState = net.minecraft.world.level.block.Blocks.PISTON_HEAD.defaultBlockState()
                            .setValue(net.minecraft.world.level.block.piston.PistonHeadBlock.FACING, facing)
                            .setValue(net.minecraft.world.level.block.piston.PistonHeadBlock.TYPE,
                                    placed.is(net.minecraft.world.level.block.Blocks.STICKY_PISTON)
                                            ? net.minecraft.world.level.block.state.properties.PistonType.STICKY
                                            : net.minecraft.world.level.block.state.properties.PistonType.DEFAULT)
                            .setValue(net.minecraft.world.level.block.piston.PistonHeadBlock.SHORT, false);
                    level.setBlock(headPos, headState, 3);
                    BlockState baseNow = level.getBlockState(pos);
                    if (baseNow.getBlock() instanceof PistonBaseBlock && !baseNow.getValue(PistonBaseBlock.EXTENDED)) {
                        level.setBlock(pos, baseNow.setValue(PistonBaseBlock.EXTENDED, true), 3);
                    }
                }
            }
        }
        return result;
    }

    @WrapOperation(
            method = "place",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;updateCustomBlockEntityTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean igny_betterEasyPlaceProtocolItemStack(BlockItem instance, BlockPos pos, Level level, Player player, ItemStack stack, BlockState state, Operation<Boolean> original, @Local(argsOnly = true) BlockPlaceContext context) {
        ItemStack newStack = BetterEasyPlaceProtocolHandler.applyItemStackProtocolData(stack, context);
        if (newStack == null) {
            return original.call(instance, pos, level, player, stack, state);
        }
        return original.call(instance, pos, level, player, newStack, state);
    }
}