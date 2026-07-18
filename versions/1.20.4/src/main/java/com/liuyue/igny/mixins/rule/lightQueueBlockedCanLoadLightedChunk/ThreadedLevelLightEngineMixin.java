package com.liuyue.igny.mixins.rule.lightQueueBlockedCanLoadLightedChunk;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ThreadedLevelLightEngine.class)
public abstract class ThreadedLevelLightEngineMixin extends LevelLightEngine {

    @Shadow
    @Final
    private ChunkMap chunkMap;

    public ThreadedLevelLightEngineMixin(LightChunkGetter lightChunkGetter, boolean blockLight, boolean skyLight) {
        super(lightChunkGetter, blockLight, skyLight);
    }

    @Shadow
    public abstract void retainData(ChunkPos pos, boolean retain);

    @Shadow
    public abstract void setLightEnabled(ChunkPos pos, boolean lightEnabled);

    @Unique
    private boolean alreadyLighted(ChunkAccess chunk) {
        return chunk != null
                && chunk.getStatus().isOrAfter(ChunkStatus.LIGHT)
                && chunk.isLightCorrect();
    }

    @Inject(
            method = "initializeLight(Lnet/minecraft/world/level/chunk/ChunkAccess;Z)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipInitializeLight(
            ChunkAccess chunk,
            boolean lighted,
            CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir
    ) {
        if (!IGNYSettings.LIGHT_QUEUE_BLOCKED_CAN_LOAD_LIGHTED_CHUNK.value()) {
            return;
        }

        if (lighted && alreadyLighted(chunk)) {
            ChunkPos pos = chunk.getPos();
            super.setLightEnabled(pos, true);
            super.retainData(pos, false);
            cir.setReturnValue(CompletableFuture.completedFuture(chunk));
        }
    }

    @Inject(
            method = "lightChunk(Lnet/minecraft/world/level/chunk/ChunkAccess;Z)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipLightChunk(
            ChunkAccess chunk,
            boolean lighted,
            CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir
    ) {
        if (!IGNYSettings.LIGHT_QUEUE_BLOCKED_CAN_LOAD_LIGHTED_CHUNK.value()) {
            return;
        }

        if (lighted && alreadyLighted(chunk)) {
            ChunkPos pos = chunk.getPos();
            chunk.setLightCorrect(true);

            ((ChunkMapInvoker)this.chunkMap).invokeReleaseLightTicket(pos);

            cir.setReturnValue(CompletableFuture.completedFuture(chunk));
        }
    }

    @Inject(
            method = "waitForPendingTasks(II)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipPendingTasks(
            int chunkX,
            int chunkZ,
            CallbackInfoReturnable<CompletableFuture<?>> cir
    ) {
        if (!IGNYSettings.LIGHT_QUEUE_BLOCKED_CAN_LOAD_LIGHTED_CHUNK.value()) {
            return;
        }

        long key = ChunkPos.asLong(chunkX, chunkZ);
        ChunkHolder holder = ((ChunkMapInvoker) this.chunkMap)
                .invokeGetVisibleChunkIfPresent(key);

        if (holder == null) {
            return;
        }

        ChunkAccess chunk = holder.getLastAvailable();
        if (alreadyLighted(chunk)) {
            cir.setReturnValue(CompletableFuture.completedFuture(null));
        }
    }
}