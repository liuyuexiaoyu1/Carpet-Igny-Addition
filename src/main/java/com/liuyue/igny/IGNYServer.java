package com.liuyue.igny;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import com.liuyue.igny.commands.*;
import com.liuyue.igny.logging.IGNYLoggerRegistry;
import com.liuyue.igny.network.packet.PacketUtil;
import com.liuyue.igny.utils.ComponentTranslate;
import com.liuyue.igny.utils.CountRulesUtil;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//#if MC < 12005
//$$ import net.minecraft.resources.ResourceLocation;
//#endif

import java.util.Map;

public class IGNYServer implements CarpetExtension {
    public static long serverStartTimeMillis;
    public static final int ruleCount = CountRulesUtil.countRules();
    public static final String fancyName = "Carpet IGNY Addition";
    public static final String MOD_ID = IGNYServerMod.getModId();
//    public static final String compactName = MOD_ID.replace("-", "");
    public static final Logger LOGGER = LogManager.getLogger(fancyName);
    private static MinecraftServer minecraftServer;
    public static SettingsManager settingsManager;
    private static final IGNYServer INSTANCE = new IGNYServer();
    //#if MC < 12005
    //$$ public static final ResourceLocation HIGHLIGHT_PACKET_ID = new ResourceLocation(MOD_ID, "highlight_block");
    //$$ public static final ResourceLocation REMOVE_HIGHLIGHT_PACKET_ID = new ResourceLocation(MOD_ID, "remove_highlight_block");
    //$$ public static final ResourceLocation SYNC_STACK_SIZE_PACKET_ID = new ResourceLocation(MOD_ID, "sync_custom_stack_size");
    //#endif
    public static IGNYServer getInstance() {
        return INSTANCE;
    }

    public MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }

    public static void init() {
        CarpetServer.manageExtension(INSTANCE);
    }

    @Override
    public void onGameStarted() {
        settingsManager = new SettingsManager(IGNYServer.getInstance().version(), MOD_ID, "IGNY");
        CarpetServer.settingsManager.parseSettingsClass(IGNYSettings.class);
    }

    @Override
    public void registerLoggers() {
        IGNYLoggerRegistry.registerLoggers();
    }

    @Override
    public void registerCommands(
            CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext
    ) {
        FixnotepitchCommmand.register(dispatcher);
        PlayerOperateCommand.register(dispatcher);
        ClearLightQueueCommand.register(dispatcher);
        CustomPlayerPickupItemCommand.register(dispatcher, commandBuildContext);
        CustomItemMaxStackSizeCommand.register(dispatcher, commandBuildContext);
        IGNYCommand.register(dispatcher);
    }

    @Override
    public String version() {
        return IGNYServerMod.getModId();
    }


    @Override
    public void onServerLoaded(MinecraftServer server) {
        minecraftServer = server;
        serverStartTimeMillis = System.currentTimeMillis();
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return ComponentTranslate.getTranslationFromResourcePath(lang);
    }

    //#if MC >= 12006
    @Override
    public void onPlayerLoggedIn(ServerPlayer player) {
        PacketUtil.sendCustomStackSizeToClient(player);
    }
    //#endif
}
