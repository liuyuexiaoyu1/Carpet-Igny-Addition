package com.liuyue.igny.mixins.rule.easyPlaceNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockItem.class, priority = 950)
public abstract class BlockItemMixin extends Item {
    private BlockItemMixin(Item.Properties properties) {
        super(properties);
    }

    @Inject(method = "getPlacementState", at = @At("HEAD"))
    private void igny_detectEasyPlaceProtocol(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        IGNYSettings.easyPlaceProtocolActive.set(false);

        String mode = IGNYSettings.EASY_PLACE_NO_BLOCK_UPDATE.value();
        if ("false".equals(mode)) return;

        // 检测是否为轻松放置协议放置的方块
        Vec3 hitVec = context.getClickLocation();
        double dx = hitVec.x - (double) context.getClickedPos().getX();
        if (dx < 2.0) return;

        // 检查触发条件（蹲下/站立/总是）
        String condition = IGNYSettings.EASY_PLACE_NO_BLOCK_UPDATE_CONDITION.value();
        Player player = context.getPlayer();
        if (player != null) {
            switch (condition) {
                case "sneaking":
                    if (!player.isShiftKeyDown()) return;
                    break;
                case "standing":
                    if (player.isShiftKeyDown()) return;
                    break;
                default: // "always"
                    break;
            }
        }

        IGNYSettings.easyPlaceProtocolActive.set(true);
    }

    @Inject(method = "place", at = @At("RETURN"))
    private void igny_clearProtocolFlag(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        IGNYSettings.easyPlaceProtocolActive.set(false);
    }
}
