package com.liuyue.igny.utils.rule.playerOperationLimiter;

import net.minecraft.server.level.ServerPlayer;

public interface SafeServerPlayerEntity {
    int igny$getBreakCountPerTick();
    int igny$getPlaceCountPerTick();
    void igny$addBreakCountPerTick();
    void igny$addPlaceCountPerTick();
    boolean igny$canPlace(ServerPlayer player);
    boolean igny$canBreak(ServerPlayer player);
}