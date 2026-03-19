package com.liuyue.igny.mixins.rule.betterSprintGameTick;

import com.liuyue.igny.utils.TickUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerTickRateManager.class)
public class ServerTickRateManagerMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @WrapMethod(method = "isSprinting")
    private boolean isSprinting(Operation<Boolean> original) {
        return TickUtil.shouldSprint(this.server) && original.call();
    }

    @Inject(method = "checkShouldSprintThisTick", at = @At(value = "INVOKE", target = "Ljava/lang/System;nanoTime()J"), cancellable = true)
    private void checkShouldSprintThisTick(CallbackInfoReturnable<Boolean> cir) {
        if (!TickUtil.shouldSprint(this.server)) {
            cir.setReturnValue(false);
        }
    }
}
