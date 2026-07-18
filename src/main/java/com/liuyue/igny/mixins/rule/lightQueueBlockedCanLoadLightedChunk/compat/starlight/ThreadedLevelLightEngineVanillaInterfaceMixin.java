package com.liuyue.igny.mixins.rule.lightQueueBlockedCanLoadLightedChunk.compat.starlight;

import ca.spottedleaf.starlight.common.light.StarLightInterface;
import com.liuyue.igny.IGNYSettings;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.level.chunk.ChunkAccess;
//#if MC >= 12101
import net.minecraft.world.level.chunk.status.ChunkStatus;
//#else
//$$ import net.minecraft.world.level.chunk.ChunkStatus;
//#endif
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Restriction(require = @Condition("scalablelux"))
@Mixin(targets = "ca.spottedleaf.starlight.common.light.vanillainterface.ThreadedLevelLightEngineVanillaInterface", remap = false)
public abstract class ThreadedLevelLightEngineVanillaInterfaceMixin {
    @Shadow(remap = false) @Final
    protected StarLightInterface lightEngine;

    @Unique
    private static boolean alreadyLighted(ChunkAccess chunk) {
        return chunk != null
                //#if MC >= 12101
                && chunk.getPersistedStatus().isOrAfter(ChunkStatus.LIGHT)
                //#else
                //$$ && chunk.getStatus().isOrAfter(ChunkStatus.LIGHT)
                //#endif
                && chunk.isLightCorrect();
    }

    @Inject(
            method = "lightChunk(Lnet/minecraft/world/level/chunk/ChunkAccess;Z)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void skipLitChunkRelight(
            ChunkAccess chunk,
            boolean lit,
            CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir
    ) {
        if (IGNYSettings.LIGHT_QUEUE_BLOCKED_CAN_LOAD_LIGHTED_CHUNK.value() && lit && alreadyLighted(chunk)) {
            chunk.setLightCorrect(true);
            cir.setReturnValue(CompletableFuture.completedFuture(chunk));
        }
    }

    @Inject(
            method = "waitForPendingTasks(II)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void skipSyncFuture(
            int chunkX,
            int chunkZ,
            CallbackInfoReturnable<CompletableFuture<?>> cir
    ) {
        ChunkAccess chunk = this.lightEngine.getAnyChunkNow(chunkX, chunkZ);

        if (IGNYSettings.LIGHT_QUEUE_BLOCKED_CAN_LOAD_LIGHTED_CHUNK.value() && alreadyLighted(chunk)) {
            cir.setReturnValue(CompletableFuture.completedFuture(null));
        }
    }
}