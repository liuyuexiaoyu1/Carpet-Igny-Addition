package com.liuyue.igny.mixins.rule.lightQueueBlockedCanLoadLightedChunk;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.RuleUtil;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(value = ThreadedLevelLightEngine.class, priority = 2000)
public abstract class ThreadedLevelLightEngineMixin extends LevelLightEngine {
    @Shadow @Final
    private ChunkMap chunkMap;

    private ThreadedLevelLightEngineMixin(
            LightChunkGetter chunkSource,
            boolean hasBlockLight,
            boolean hasSkyLight
    ) {
        super(chunkSource, hasBlockLight, hasSkyLight);
    }

    @Unique
    private static boolean alreadyLighted(ChunkAccess chunk) {
        return chunk != null
                && chunk.getPersistedStatus().isOrAfter(ChunkStatus.LIGHT)
                && chunk.isLightCorrect();
    }

    @Inject(
            method = "runUpdate",
            at = @At("HEAD"),
            cancellable = true,
            order = 900
    )
    private void cancelBeforeTisYield(CallbackInfo ci) {
        Object ruleValue = RuleUtil.getCarpetRulesValue("carpet-tis-addition", "lightUpdates");
        if (ruleValue instanceof Enum<?> e) {
            String name = e.name();
            if ("suppressed".equalsIgnoreCase(name) || "off".equalsIgnoreCase(name)) {
                ci.cancel();
            }
        }
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
        if (lighted && IGNYSettings.LIGHT_QUEUE_BLOCKED_CAN_LOAD_LIGHTED_CHUNK.value() && alreadyLighted(chunk)) {
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
        if (lighted && IGNYSettings.LIGHT_QUEUE_BLOCKED_CAN_LOAD_LIGHTED_CHUNK.value() && alreadyLighted(chunk)) {
            chunk.setLightCorrect(true);
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
        ChunkHolder holder = ((ChunkMapInvoker)this.chunkMap)
                //#if MC >= 26.1
                //$$ .invokeGetVisibleChunkIfPresent(ChunkPos.pack(chunkX, chunkZ));
                //#else
                .invokeGetVisibleChunkIfPresent(ChunkPos.asLong(chunkX, chunkZ));
        //#endif
        //#if MC <= 12006
        //$$ ChunkAccess chunk = holder == null ? null : holder.getLastAvailable();
        //#else
        ChunkAccess chunk = holder == null ? null : holder.getChunkIfPresent(ChunkStatus.LIGHT);
        //#endif

        if (IGNYSettings.LIGHT_QUEUE_BLOCKED_CAN_LOAD_LIGHTED_CHUNK.value() && alreadyLighted(chunk)) {
            cir.setReturnValue(CompletableFuture.completedFuture(null));
        }
    }
}