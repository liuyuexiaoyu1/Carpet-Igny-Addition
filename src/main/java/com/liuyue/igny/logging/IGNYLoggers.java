package com.liuyue.igny.logging;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import com.liuyue.igny.logging.annotation.ObserveLogger;
import com.liuyue.igny.logging.callback.LoggerCallback;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class IGNYLoggers {
    private static final Map<String, LoggerCallback> callbacks = new HashMap<>();

    @com.liuyue.igny.logging.annotation.Logger(
            defaultValue = "",
            options = "",
            strictOptions = false
    )
    public static boolean piston;

    @com.liuyue.igny.logging.annotation.Logger(
            defaultValue = "0x32FF0000",
            options = {"0x32FF0000", "0x16FF0000", "0x32FFFFFF"},
            strictOptions = false
    )
    public static boolean allFurnace;

    @com.liuyue.igny.logging.annotation.Logger(
            defaultValue = "",
            options = "",
            strictOptions = false
    )
    public static boolean beacon;

    public static void registerLoggers() {
        for (Field field : IGNYLoggers.class.getDeclaredFields()) {
            boolean isObserved = field.isAnnotationPresent(ObserveLogger.class);
            if (!field.isAnnotationPresent(com.liuyue.igny.logging.annotation.Logger.class)) {
                if (isObserved) {
                    throw new RuntimeException("LoggerCallback annotation can only be used on Logger annotations");
                }
                continue;
            }
            com.liuyue.igny.logging.annotation.Logger anno = field.getAnnotation(com.liuyue.igny.logging.annotation.Logger.class);
            String defaultValue = anno.defaultValue();
            if (defaultValue.isEmpty()) {
                defaultValue = null;
            }
            String[] options = anno.options();
            if (options[0].isEmpty()) {
                options = null;
            }
            boolean strictOptions = anno.strictOptions();
            LoggerRegistry.registerLogger(field.getName(), new Logger(field, field.getName(), defaultValue, options, strictOptions));
            if (isObserved) {
                try {
                    ObserveLogger observeLogger = field.getAnnotation(ObserveLogger.class);
                    Class<? extends LoggerCallback> clazz = observeLogger.value();
                    var constructor = clazz.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    LoggerCallback instance = constructor.newInstance();
                    callbacks.put(field.getName(), instance);
                } catch (Exception ignored) {}
            }
        }
    }

    public static void handleChange(MinecraftServer server, Logger logger, String playerName, String option, boolean subscribe) {
        LoggerCallback callback = callbacks.get(logger.getLogName());
        if (callback != null) {
            if (subscribe) {
                callback.onSubscribe(logger, server.getPlayerList().getPlayerByName(playerName), option);
            } else {
                callback.onUnsubscribe(logger, playerName);
            }
        }
    }
}
