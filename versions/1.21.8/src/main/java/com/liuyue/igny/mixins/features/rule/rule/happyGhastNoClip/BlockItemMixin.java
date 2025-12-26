package com.liuyue.igny.mixins.features.rule.rule.happyGhastNoClip;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockItem.class, priority = 1100)
public abstract class BlockItemMixin {
    @Shadow
    protected abstract boolean mustSurvive();

    @Inject(method="canPlace", at = @At("RETURN"), cancellable = true)
    private void canPlace(BlockPlaceContext blockPlaceContext, BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
       Player player = blockPlaceContext.getPlayer();
       CollisionContext collisionContext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        if (!this.mustSurvive() || blockState.canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos()) && !blockPlaceContext.getLevel().isUnobstructed(blockState, blockPlaceContext.getClickedPos(), collisionContext)){
            if (player.getRootVehicle() instanceof HappyGhast && IGNYSettings.happyGhastNoClip){
                cir.setReturnValue(true);
            }
        }
   }
}
