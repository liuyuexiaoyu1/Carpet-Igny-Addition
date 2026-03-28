package com.liuyue.igny.logging.callback;

import carpet.logging.Logger;
import net.minecraft.server.level.ServerPlayer;

public interface LoggerCallback {
    void onSubscribe(Logger logger, ServerPlayer player, String option);
    void onUnsubscribe(Logger logger, String playerName);
}
