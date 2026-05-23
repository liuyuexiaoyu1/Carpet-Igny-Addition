package com.liuyue.igny.mixins.rule.theEndCanCreateNetherPortal;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {
    @Inject(method = "inPortalDimension", at = @At(value = "HEAD"), cancellable = true)
    private static void inPortalDimension(Level level, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.THE_END_CAN_CREATE_NETHER_PORTAL.value()) cir.setReturnValue(true);
    }
}
