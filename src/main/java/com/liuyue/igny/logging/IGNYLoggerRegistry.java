package com.liuyue.igny.logging;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;

public class IGNYLoggerRegistry {
    public static boolean __piston;

    public static void registerLoggers() {
        LoggerRegistry.registerLogger("piston", stardardLogger("piston", null, null, false));
    }

    private static Logger stardardLogger(String logName, String def, String [] options, boolean strictOptions) {
        try {
            return new Logger(IGNYLoggerRegistry.class.getField("__" + logName), logName, def, options, strictOptions);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to create logger "+logName);
        }
    }
}
