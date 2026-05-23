package com.liuyue.igny.mixins.rule.endGatewayPortalNoCooldown;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TheEndGatewayBlockEntity.class)
public class TheEndGatewayBlockEntityMixin {
    @Inject(method = "triggerCooldown", at = @At(value = "HEAD"), cancellable = true)
    private static void triggerCooldown(Level level, BlockPos pos, BlockState state, TheEndGatewayBlockEntity blockEntity, CallbackInfo ci) {
        if (IGNYSettings.END_GATEWAY_PORTAL_NO_COOLDOWN.value()) {
            ci.cancel();
        }
    }
}
