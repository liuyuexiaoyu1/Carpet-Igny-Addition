package com.liuyue.igny.helper.playerOperationLimiter;

import net.minecraft.server.level.ServerPlayer;

public interface SafeServerPlayerEntity {
    int igny$getBreakCountPerTick();
    int igny$getPlaceCountPerTick();
    void igny$addBreakCountPerTick();
    void igny$addPlaceCountPerTick();
    boolean igny$canPlace(ServerPlayer player);
    boolean igny$canBreak(ServerPlayer player);
}