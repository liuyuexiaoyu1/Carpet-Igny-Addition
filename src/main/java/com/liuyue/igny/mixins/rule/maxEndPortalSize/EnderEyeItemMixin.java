package com.liuyue.igny.mixins.rule.maxEndPortalSize;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.EndPortalShape;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public class EnderEyeItemMixin {
    @Inject(
            method = "useOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/EndPortalFrameBlock;getOrCreatePortalShape()Lnet/minecraft/world/level/block/state/pattern/BlockPattern;"
            ),
            cancellable = true)
    private void onEyePlaced(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir, @Local Level level, @Local BlockPos pos) {
        if (IGNYSettings.MAX_END_PORTAL_SIZE.value() != -1) {
            EndPortalShape.findFromFrame(level, pos).ifPresent(EndPortalShape::createPortal);
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }
}