package com.liuyue.igny.mixins.rule.betterSprintGameTick;

import carpet.helpers.ServerTickRateManager;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.liuyue.igny.utils.TickUtil;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerTickRateManager.class)
public class ServerTickRateManagerMixin {
    @Shadow private MinecraftServer server;

    @WrapMethod(method = "isInWarpSpeed")
    private boolean isInWarpSpeed(Operation<Boolean> original) {
        return TickUtil.shouldSprint(this.server) && original.call();
    }

    @Inject(method = "continueWarp", at = @At(value = "HEAD"), cancellable = true)
    private void checkShouldSprintThisTick(CallbackInfoReturnable<Boolean> cir) {
        if (!TickUtil.shouldSprint(this.server)) {
            cir.setReturnValue(false);
        }
    }
}
