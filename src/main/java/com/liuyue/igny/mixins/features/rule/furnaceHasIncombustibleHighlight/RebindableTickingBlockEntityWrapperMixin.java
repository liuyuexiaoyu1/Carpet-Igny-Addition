package com.liuyue.igny.mixins.features.rule.furnaceHasIncombustibleHighlight;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.level.chunk.LevelChunk$RebindableTickingBlockEntityWrapper")
public abstract class RebindableTickingBlockEntityWrapperMixin {
    @Shadow
    public abstract BlockPos getPos();

    @Shadow
    abstract void rebind(TickingBlockEntity tickingBlockEntity);

    @Shadow
    private TickingBlockEntity ticker;

    @Unique
    private TickingBlockEntity igny$originalTicker;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(
            //#if MC < 12104
            LevelChunk this$0,
            //#endif
                        TickingBlockEntity tickingBlockEntity1, CallbackInfo ci) {
        this.igny$originalTicker = tickingBlockEntity1;
    }

    @Inject(method = "rebind", at = @At("HEAD"), cancellable = true)
    private void onRebind(final TickingBlockEntity newTicker, CallbackInfo ci) {
        if (IGNYSettings.furnaceHasIncombustibleHighlight && newTicker.getType().equals("<lithium_sleeping>")) {
            ci.cancel();
        }
    }

    @Inject(method = "getPos", at = @At("HEAD"))
    private void onReloadChunk(CallbackInfoReturnable<BlockPos> cir) {
        if (IGNYSettings.furnaceHasIncombustibleHighlight && this.ticker.getType().equals("<lithium_sleeping>")) {
            this.rebind(this.igny$originalTicker);
        }
    }
}
