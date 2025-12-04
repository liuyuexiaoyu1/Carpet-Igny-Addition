package com.liuyue.igny.utils;

import com.liuyue.igny.IGNYServer;
import net.fabricmc.loader.api.FabricLoader;

public class AutoMixinAuditExecutor {
    private static final String KEYWORD_PROPERTY = "carpetignyaddition.mixin_audit";
    public static void run() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment() && "true".equals(System.getProperty(KEYWORD_PROPERTY))) {
            IGNYServer.LOGGER.info("Triggered auto mixin audit");
            boolean ok = MixinUtil.audit(null);
            IGNYServer.LOGGER.info("Mixin audit result: {}", ok ? "successful" : "failed");
            System.exit(ok ? 0 : 1);
        }
    }
}