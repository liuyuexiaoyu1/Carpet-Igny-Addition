package com.liuyue.igny.mixins.rule.scalableLuxCompatible;

import ca.spottedleaf.starlight.common.light.vanillainterface.ThreadedLevelLightEngineVanillaInterface;
import com.liuyue.igny.IGNYSettings;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

//#if MC >= 12101
@Restriction(require = @Condition("scalablelux"))
//#else
//$$ @Restriction(require = @Condition("starlight"))
//#endif
@Mixin(ThreadedLevelLightEngineVanillaInterface.class)
public class ThreadedLevelLightEngineVanillaInterfaceMixin {
    @Inject(method = "lightChunk", at = @At(value = "HEAD"), cancellable = true)
    private void lightChunk(ChunkAccess chunk, boolean lit, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        if (lit && IGNYSettings.SCALABLELUX_COMPATIBLE.value()) {
            cir.setReturnValue(CompletableFuture.completedFuture(chunk));
        }
    }
}
