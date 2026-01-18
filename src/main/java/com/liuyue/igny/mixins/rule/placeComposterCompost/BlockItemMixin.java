package com.liuyue.igny.mixins.rule.placeComposterCompost;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @ModifyVariable(
            method = "place",
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private BlockState getPlacementState(BlockState originalState, BlockPlaceContext ctx) {
        if (originalState != null && originalState.is(Blocks.COMPOSTER)) {
            if (ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown()) {
                int level = Math.min(8, Math.max(0, IGNYSettings.placeComposterCompost));
                return originalState.setValue(ComposterBlock.LEVEL, level);
            }
        }
        return originalState;
    }
}
