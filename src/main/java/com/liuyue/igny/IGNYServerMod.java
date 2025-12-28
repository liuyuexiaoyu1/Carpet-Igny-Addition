package com.liuyue.igny;

import com.liuyue.igny.network.packet.PacketRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IGNYServerMod implements ModInitializer {
    private static final String MOD_ID = "carpet_igny_addition";
    private static String version;
    public static final List<String> CARPET_ADDITION_MOD_IDS;

    static {
        ArrayList<String> mods = new ArrayList<>();
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = modContainer.getMetadata();
            Collection<ModDependency> dependencies = metadata.getDependencies();
            if (dependencies.stream().map(ModDependency::getModId).toList().contains("carpet")){
                mods.add(metadata.getId());
            }
        }
        CARPET_ADDITION_MOD_IDS = mods;
    }

    @Override
    public void onInitialize() {
        version = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
        //#if MC >= 12005
        PacketRegistry.s2c();
        //#endif
        IGNYServer.init();
    }

    public static String getModId() {
        return MOD_ID;
    }

    public static String getVersion() {
        return version;
    }
}
