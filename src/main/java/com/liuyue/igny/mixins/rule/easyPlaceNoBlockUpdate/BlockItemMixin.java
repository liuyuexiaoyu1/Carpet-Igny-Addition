package com.liuyue.igny.mixins.rule.easyPlaceNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockItem.class, priority = 950)
public abstract class BlockItemMixin extends Item {
    private BlockItemMixin(Item.Properties properties) {
        super(properties);
    }

    @Shadow public abstract Block getBlock();

    @Inject(method = "getPlacementState", at = @At("HEAD"))
    private void igny_detectEasyPlaceProtocol(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        IGNYSettings.easyPlaceProtocolActive.set(false);

        if (!IGNYSettings.EASY_PLACE_NO_BLOCK_UPDATE.value()) return;

        // 检测是否为轻松放置协议放置的方块
        // 协议编码: protocolValue = (int)(hitVec.x - pos.x) - 2
        // 正常点击: hitVec.x 在 pos.x ~ pos.x+1 之间
        // 协议放置: hitVec.x = pos.x + 2 + protocolValue
        Vec3 hitVec = context.getClickLocation();
        double dx = hitVec.x - (double) context.getClickedPos().getX();

        if (dx >= 2.0) {
            IGNYSettings.easyPlaceProtocolActive.set(true);
        }
    }

    @Inject(method = "place", at = @At("RETURN"))
    private void igny_clearProtocolFlag(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        IGNYSettings.easyPlaceProtocolActive.set(false);
    }
}
