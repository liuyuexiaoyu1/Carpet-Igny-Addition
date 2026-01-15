
package com.liuyue.igny.mixins.interfaces;

import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.data.CustomPickupDataManager;
import com.liuyue.igny.data.CustomItemMaxStackSizeDataManager;
import com.liuyue.igny.task.ITask;
import com.liuyue.igny.task.TaskManager;
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
            CustomPickupDataManager.setServer(server);
            //#if MC >= 12006
            CustomItemMaxStackSizeDataManager.setServer(server);
            //#endif
        }
    }

    @Inject(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickChildren(Ljava/util/function/BooleanSupplier;)V"))
    private void tickServer(CallbackInfo ci){
        for (ITask task : TaskManager.getAllActiveTasks()) {
            task.tick();
        }
    }
}
