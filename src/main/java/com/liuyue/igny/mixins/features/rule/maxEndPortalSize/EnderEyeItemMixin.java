package com.liuyue.igny.mixins.features.rule.maxEndPortalSize;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.EndPortalShape;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public class EnderEyeItemMixin {
    @Inject(
            method = "useOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/EndPortalFrameBlock;getOrCreatePortalShape()Lnet/minecraft/world/level/block/state/pattern/BlockPattern;",
                    shift = At.Shift.AFTER
            )
    )
    private void onEyePlaced(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        var level = ctx.getLevel();
        var pos = ctx.getClickedPos();
        if (level.isClientSide()) return;
        EndPortalShape.findFromFrame(level, pos).ifPresent(EndPortalShape::createPortal);
    }

    @ModifyVariable(
            method = "useOn",
            at = @At("STORE"),
            ordinal = 0
    )
    private BlockPattern.BlockPatternMatch disableVanillaActivation(BlockPattern.BlockPatternMatch original) {
        return IGNYSettings.maxEndPortalSize != -1 ? null : original;
    }
}