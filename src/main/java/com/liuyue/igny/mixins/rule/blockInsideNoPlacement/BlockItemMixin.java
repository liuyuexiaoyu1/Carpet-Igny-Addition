package com.liuyue.igny.mixins.rule.blockInsideNoPlacement;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.NoClipHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockItem.class, priority = 1050)
public class BlockItemMixin {

    @Inject(method = "canPlace", at = @At("HEAD"), cancellable = true)
    private void igny$noPlacementInsideBlock(BlockPlaceContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!IGNYSettings.BLOCK_INSIDE_NO_PLACEMENT.value()) {
            return;
        }

        Player player = context.getPlayer();

        if (player == null || !NoClipHelper.isNoClipping(player)) {
            return;
        }

        Level level = context.getLevel();

        if (NoClipHelper.isEyeInsideBlock(player, level)) {
            cir.setReturnValue(false);
        }
    }
}
