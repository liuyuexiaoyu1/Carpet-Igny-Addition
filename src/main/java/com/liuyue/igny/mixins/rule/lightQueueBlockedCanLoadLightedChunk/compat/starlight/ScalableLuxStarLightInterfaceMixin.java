package com.liuyue.igny.mixins.rule.lightQueueBlockedCanLoadLightedChunk.compat.starlight;

import ca.spottedleaf.starlight.common.light.StarLightInterface;
import com.liuyue.igny.IGNYSettings;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

//#if MC >= 12101
@Restriction(require = @Condition("scalablelux"))
//#else
//$$ @Restriction(require = @Condition("starlight"))
//#endif
@Mixin(StarLightInterface.class)
public abstract class ScalableLuxStarLightInterfaceMixin {
    @Shadow
    public abstract ChunkAccess getAnyChunkNow(int chunkX, int chunkZ);

    @Inject(
            method = "syncFuture(II)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void allowAlreadyLightCorrectChunk(int chunkX, int chunkZ, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (IGNYSettings.LIGHT_QUEUE_BLOCKED_CAN_LOAD_LIGHTED_CHUNK.value()) {
            ChunkAccess chunk = this.getAnyChunkNow(chunkX, chunkZ);
            if (chunk != null
                    && chunk.getPersistedStatus().isOrAfter(ChunkStatus.LIGHT)
                    && chunk.isLightCorrect()) {
                cir.setReturnValue(CompletableFuture.completedFuture(null));
            }
        }
    }
}