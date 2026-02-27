package com.liuyue.igny.mixins.rule.disableWatchDog;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerWatchdog;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerWatchdog.class)
public class ServerWatchdogMixin {
    @Shadow
    @Final
    private DedicatedServer server;

    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getNanos()J"))
    private long getNanos(Operation<Long> original) {
        if (IGNYSettings.disableWatchDog) return this.server.getNextTickTime();
        return original.call();
    }
}
