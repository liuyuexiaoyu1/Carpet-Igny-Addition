package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.getRelativeHitZ;
import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.isProtocol;

@Mixin(value = StandingAndWallBlockItem.class, priority = 950)
public abstract class StandingAndWallBlockItemMixin extends BlockItem {
    @Shadow
    @Final
    protected Block wallBlock;

    protected StandingAndWallBlockItemMixin(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    private void igny_betterEasyPlaceProtocolDecode(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!BetterEasyPlaceProtocolHandler.isRuleEnabled()) return;

        double relativeHitZ = getRelativeHitZ(context.getClickLocation(), context.getClickedPos());
        if (!isProtocol(relativeHitZ)) return;

        BlockState state = BetterEasyPlaceProtocolHandler.decodeAttachablePlacementState(this.getBlock(), this.wallBlock, context);
        if (state == null) {
            cir.setReturnValue(null);
            return;
        }
        if (!this.canPlace(context, state)) return;
        cir.setReturnValue(state);
    }
}