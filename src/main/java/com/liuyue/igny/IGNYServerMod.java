package com.liuyue.igny;



import com.liuyue.igny.utils.AutoMixinAuditExecutor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class IGNYServerMod implements ModInitializer {
    private static final String MOD_ID = "carpet_igny_addition";
    private static String version;

    @Override
    public void onInitialize() {
        version = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
        AutoMixinAuditExecutor.run();
        IGNYServer.init();
    }

    public static String getModId() {
        return MOD_ID;
    }

    public static String getVersion() {
        return version;
    }
}
