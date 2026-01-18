package com.liuyue.igny.mixins.rule.playerOperationLimiter;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.rule.playerOperationLimiter.SafeServerPlayerEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {
    public BlockItemMixin(Properties settings) {
        super(settings);
    }

    @Shadow
    protected abstract BlockState getPlacementState(BlockPlaceContext context);

    @Shadow
    public abstract @Nullable BlockPlaceContext updatePlacementContext(BlockPlaceContext context);

    @Inject(
            method = "place",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkPlaceOperationLimit(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!IGNYSettings.playerOperationLimiter) {
            return;
        }
        if (context.canPlace() && context.getPlayer() instanceof ServerPlayer serverPlayer) {
            BlockPlaceContext updated = this.updatePlacementContext(context);
            if (updated != null && this.getPlacementState(updated) != null) {
                SafeServerPlayerEntity safe = (SafeServerPlayerEntity) serverPlayer;
                if (!safe.igny$canPlace(serverPlayer)) {
                    safe.igny$addPlaceCountPerTick();
                    cir.setReturnValue(InteractionResult.FAIL);
                }
            }
        }
    }
}