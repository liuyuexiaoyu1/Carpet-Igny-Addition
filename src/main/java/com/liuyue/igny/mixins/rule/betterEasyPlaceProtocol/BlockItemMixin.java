package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.ISignBlockEntity;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.decodeProtocolValueFromHitDim;
import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.getRelativeHitX;
import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.getRelativeHitZ;
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
        double relativeHitZ = getRelativeHitZ(context.getClickLocation(), context.getClickedPos());
        if (!isProtocol(relativeHitZ)) return;

        BlockState baseState = cir.getReturnValue();
        if (baseState == null) {
            baseState = this.getBlock().getStateForPlacement(context);
        }
        BlockState state = BetterEasyPlaceProtocolHandler.decodePlacementState(this.getBlock(), context, baseState);
        if (state == null) {
            cir.setReturnValue(null);
            return;
        }
        if (!this.canPlace(context, state)) return;
        cir.setReturnValue(state);
    }

    @WrapMethod(method = "place")
    private InteractionResult igny_setPlaceState(BlockPlaceContext context, Operation<InteractionResult> original) {
        if (BetterEasyPlaceProtocolHandler.isRuleEnabled() && isProtocol(getRelativeHitX(context.getClickLocation(), context.getClickedPos()))) {
            BetterEasyPlaceProtocolHandler.setEasyPlaceState(true);
            BetterEasyPlaceProtocolHandler.setPlaceTargetPos(context.getClickedPos());
            BetterEasyPlaceProtocolHandler.setPlaceTargetBlock(this.getBlock());
        }
        try {
            return original.call(context);
        } finally {
            BetterEasyPlaceProtocolHandler.setEasyPlaceState(false);
            BetterEasyPlaceProtocolHandler.setPlaceProperty(0);
            BetterEasyPlaceProtocolHandler.setPlaceTargetPos(BlockPos.ZERO);
            BetterEasyPlaceProtocolHandler.setPlaceTargetBlock(Blocks.AIR);
        }
    }

    @WrapOperation(
            method = "place",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean igny_onPlace(BlockItem instance, BlockPlaceContext context, BlockState state, Operation<Boolean> original) {
        if (state.getBlock() instanceof PistonBaseBlock && state.getValue(PistonBaseBlock.EXTENDED)) {
            BlockPos headPos = context.getClickedPos().relative(state.getValue(PistonBaseBlock.FACING));
            BlockState headTarget = context.getLevel().getBlockState(headPos);
            if (!headTarget.isAir() && !headTarget.canBeReplaced()) {
                state = state.setValue(PistonBaseBlock.EXTENDED, false);
            }
        }

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
                    try {
                        if (level.setBlock(headPos, headState, 3)) {
                            IGNYSettings.lastPistonHeadPos = headPos;
                        }
                    } catch (Exception ignored) {}
                    BlockState baseNow = level.getBlockState(pos);
                    if (baseNow.getBlock() instanceof PistonBaseBlock && !baseNow.getValue(PistonBaseBlock.EXTENDED)) {
                        level.setBlock(pos, baseNow.setValue(PistonBaseBlock.EXTENDED, true), 2 | 16);
                    }
                }
            }
        }
        return result;
    }

    //#if MC >= 26.3
    //$$ @WrapOperation(
    //$$          method = "place",
    //$$          at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;updateCustomBlockEntityTag(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Z")
    //$$  )
    //$$  private boolean igny_betterEasyPlaceProtocolItemStack(Level level, Player player, BlockPos pos, ItemStack stack, Operation<Boolean> original, @Local(argsOnly = true) BlockPlaceContext context)
    //#else
    @WrapOperation(
            method = "place",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;updateCustomBlockEntityTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean igny_betterEasyPlaceProtocolItemStack(BlockItem instance, BlockPos pos, Level level, Player player, ItemStack stack, BlockState state, Operation<Boolean> original, @Local(argsOnly = true) BlockPlaceContext context)
    //#endif
    {
        ItemStack newStack = BetterEasyPlaceProtocolHandler.applyItemStackProtocolData(stack, context);
        if (newStack == null) {
            //#if MC >= 26.3
            //$$ return original.call(level, player, pos, stack);
            //#else
            return original.call(instance, pos, level, player, stack, state);
            //#endif
        }
        //#if MC >= 26.3
        //$$ boolean result = original.call(level, player, pos, newStack);
        //#else
        boolean result = original.call(instance, pos, level, player, newStack, state);
        //#endif
        //#if MC >= 12001
        double relativeHitZ = getRelativeHitZ(context.getClickLocation(), context.getClickedPos());
        int protocolValue = decodeProtocolValueFromHitDim(relativeHitZ);
        if ((protocolValue & 0b100_0000_0000) != 0) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SignBlockEntity sbe) {
                ((ISignBlockEntity) sbe).igny$setPendingWaxed(true);
            }
        }
        //#endif
        return result;
    }
}
