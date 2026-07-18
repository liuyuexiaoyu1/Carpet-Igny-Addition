package com.liuyue.igny.mixins.rule.scalableLuxCompatible;

import com.liuyue.igny.IGNYSettings;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Restriction(require = @Condition("scalablelux"))
@Mixin(targets = "ca.spottedleaf.starlight.common.light.vanillainterface.ThreadedLevelLightEngineVanillaInterface", remap = false)
public class ThreadedLevelLightEngineVanillaInterfaceMixin {
    @Inject(method = "lightChunk", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void lightChunk(ChunkAccess chunk, boolean lit, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        if (lit && IGNYSettings.SCALABLELUX_COMPATIBLE.value()) {
            cir.setReturnValue(CompletableFuture.completedFuture(chunk));
        }
    }
}