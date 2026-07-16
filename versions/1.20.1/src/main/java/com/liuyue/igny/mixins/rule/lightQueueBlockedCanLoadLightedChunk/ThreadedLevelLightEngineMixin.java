package com.liuyue.igny.mixins.rule.lightQueueBlockedCanLoadLightedChunk;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ThreadedLevelLightEngine.class)
public abstract class ThreadedLevelLightEngineMixin {
    @Shadow
    @Final
    private ChunkMap chunkMap;

    @Shadow
    public abstract void retainData(ChunkPos pos, boolean retain);

    @Inject(
            method = "lightChunk(Lnet/minecraft/world/level/chunk/ChunkAccess;Z)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipAlreadyLightedChunk(ChunkAccess chunk, boolean bl, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        if (!bl) {
            return;
        }

        ChunkPos pos = chunk.getPos();
        chunk.setLightCorrect(true);
        this.retainData(pos, false);
        ((ChunkMapInvoker) this.chunkMap).invokeReleaseLightTicket(pos);

        cir.setReturnValue(CompletableFuture.completedFuture(chunk));
    }
}