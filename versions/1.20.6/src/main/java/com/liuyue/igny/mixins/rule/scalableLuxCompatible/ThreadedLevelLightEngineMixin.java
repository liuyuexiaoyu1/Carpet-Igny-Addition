package com.liuyue.igny.mixins.rule.scalableLuxCompatible;

import com.liuyue.igny.IGNYSettings;
//#if MC <= 12004
//$$ import com.liuyue.igny.mixins.rule.lightQueueBlockedCanLoadLightedChunk.ChunkMapInvoker;
//#endif
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
//#if MC <= 12004
//$$ import net.minecraft.server.level.ChunkMap;
//$$ import net.minecraft.world.level.ChunkPos;
//#endif
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
//#if MC <= 12004
//$$ import org.spongepowered.asm.mixin.Final;
//#endif
import org.spongepowered.asm.mixin.Mixin;
//#if MC <= 12004
//$$ import org.spongepowered.asm.mixin.Shadow;
//#endif
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Restriction(require = @Condition("starlight"))
@Mixin(value = ThreadedLevelLightEngine.class, priority = 2000)
public abstract class ThreadedLevelLightEngineMixin {
    //#if MC <= 12004
    //$$ @Shadow @Final
    //$$ private ChunkMap chunkMap;
    //#endif

    @Inject(
            method = "lightChunk(Lnet/minecraft/world/level/chunk/ChunkAccess;Z)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void lightChunk(
            ChunkAccess chunk,
            boolean lit,
            CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir
    ) {
        if (lit && IGNYSettings.SCALABLELUX_COMPATIBLE.value()) {
            chunk.setLightCorrect(true);
            //#if MC <= 12004
            //$$ ChunkPos pos = chunk.getPos();
            //$$ ((ChunkMapInvoker)this.chunkMap).invokeReleaseLightTicket(pos);
            //#endif
            cir.setReturnValue(CompletableFuture.completedFuture(chunk));
        }
    }
}