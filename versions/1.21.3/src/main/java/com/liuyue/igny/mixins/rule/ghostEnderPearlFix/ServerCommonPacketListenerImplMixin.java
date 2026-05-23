package com.liuyue.igny.mixins.rule.ghostEnderPearlFix;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {
    @Shadow
    @Final
    protected Connection connection;

    @WrapOperation(method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;executeBlocking(Ljava/lang/Runnable;)V"))
    private void disconnect(MinecraftServer instance, Runnable runnable, Operation<Void> original) {
        if (IGNYSettings.GHOST_ENDER_PEARL_FIX.value()) {
            if (!instance.isStopped() || ((ConnectionAccessor) this.connection).getChannel() == null) {
                original.call(instance, runnable);
                return;
            }
            CompletableFuture<Void> lock = new CompletableFuture<>();
            ((ConnectionAccessor) this.connection).getChannel().close().addListener(future -> instance.execute(() -> {
                runnable.run();
                lock.complete(null);
            }));
            instance.managedBlock(lock::isDone);
            return;
        }
        original.call(instance, runnable);
    }
}
