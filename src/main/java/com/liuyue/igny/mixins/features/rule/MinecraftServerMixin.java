
package com.liuyue.igny.mixins.features.rule;


import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.tracker.RuleChangeTracker;
import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "loadLevel", at = @At("TAIL"))
    private void afterServerLoadWorld(CallbackInfo ci){
        MinecraftServer server = IGNYServer.getInstance().getMinecraftServer();
        if (server != null && server.isRunning()) {
            RuleChangeTracker.init(server);
        }
    }
}
