package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.tracker.ItemFlowTracker;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "tickServer", at = @At(value = "RETURN"))
    private void igny$tickItemFlowTracker(CallbackInfo ci) {
        ItemFlowTracker.tick((MinecraftServer) (Object) this);
    }
}
