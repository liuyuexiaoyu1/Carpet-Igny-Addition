package com.liuyue.igny.mixins.rule.playerOperationLimiter;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.rule.playerOperationLimiter.SafeServerPlayerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements SafeServerPlayerEntity {
    private int igny$breakCountPerTick = 0;
    private int igny$placeCountPerTick = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void resetOperationCountPerTick(CallbackInfo ci) {
        this.igny$breakCountPerTick = 0;
        this.igny$placeCountPerTick = 0;
    }

    @Override
    public int igny$getBreakCountPerTick() {
        return this.igny$breakCountPerTick;
    }

    @Override
    public int igny$getPlaceCountPerTick() {
        return this.igny$placeCountPerTick;
    }

    @Override
    public void igny$addBreakCountPerTick() {
        ++this.igny$breakCountPerTick;
    }

    @Override
    public void igny$addPlaceCountPerTick() {
        ++this.igny$placeCountPerTick;
    }

    @Override
    public boolean igny$canPlace(ServerPlayer player) {
        if (player instanceof EntityPlayerMPFake){
            return this.igny$placeCountPerTick == 0 || this.igny$placeCountPerTick < IGNYSettings.fakePlayerPlaceLimitPerTick;
        }
        return this.igny$placeCountPerTick == 0 || this.igny$placeCountPerTick < IGNYSettings.realPlayerPlaceLimitPerTick;
    }

    @Override
    public boolean igny$canBreak(ServerPlayer player) {
        if (player instanceof EntityPlayerMPFake){
            return this.igny$breakCountPerTick == 0 || this.igny$breakCountPerTick < IGNYSettings.fakePlayerBreakLimitPerTick;
        }
        return this.igny$breakCountPerTick == 0 || this.igny$breakCountPerTick < IGNYSettings.realPlayerBreakLimitPerTick;
    }
}