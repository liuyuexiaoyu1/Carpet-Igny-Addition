package com.liuyue.igny.mixins.rule.lightQueueBlockedCanLoadLightedChunk;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
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

    @Inject(
            method = "waitForPendingTasks",
            at = @At("HEAD"),
            cancellable = true
    )
    private void allowAlreadyLightCorrectChunk(int chunkX, int chunkZ, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        if (IGNYSettings.LIGHT_QUEUE_BLOCKED_CAN_LOAD_LIGHTED_CHUNK.value()) {
            long key = ChunkPos.asLong(chunkX, chunkZ);
            ChunkHolder holder = ((ChunkMapAccessor) this.chunkMap).invokeGetVisibleChunkIfPresent(key);

            if (holder == null) {
                return;
            }

            LevelChunk chunk = holder.getTickingChunk();

            if (chunk == null) {
                ChunkResult<LevelChunk> result = holder.getFullChunkFuture().getNow(null);
                if (result != null) {
                    chunk = result.orElse(null);
                }
            }

            if (chunk != null
                    && chunk.getPersistedStatus().isOrAfter(ChunkStatus.LIGHT)
                    && chunk.isLightCorrect()) {
                cir.setReturnValue(CompletableFuture.completedFuture(null));
            }
        }
    }
}