package com.liuyue.igny;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import com.liuyue.igny.commands.*;
import com.liuyue.igny.logging.IGNYLoggerRegistry;
import com.liuyue.igny.network.packet.PacketUtil;
import com.liuyue.igny.utils.ComponentTranslate;
import com.liuyue.igny.utils.CountRulesUtil;
import com.liuyue.igny.utils.TickUtil;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
//#if MC >= 12003
import net.minecraft.world.TickRateManager;
//#elseif MC > 11904
//$$ import carpet.helpers.TickRateManager;
//$$ import carpet.fakes.MinecraftServerInterface;
//#else
//$$ import carpet.helpers.TickSpeed;
//#endif
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
    private static final IGNYServer INSTANCE = new IGNYServer();

    //#if MC < 12005
    //$$ public static final ResourceLocation HIGHLIGHT_PACKET_ID = new ResourceLocation(MOD_ID, "highlight_block");
    //$$ public static final ResourceLocation REMOVE_HIGHLIGHT_PACKET_ID = new ResourceLocation(MOD_ID, "remove_highlight_block");
    //$$ public static final ResourceLocation SYNC_STACK_SIZE_PACKET_ID = new ResourceLocation(MOD_ID, "sync_custom_stack_size");
    //$$ public static final ResourceLocation RENDER_BOX_PACKET_ID = new ResourceLocation(MOD_ID, "render_box");
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


    @Override
    public void onPlayerLoggedIn(ServerPlayer player) {
        //#if MC >= 12006
        PacketUtil.sendCustomStackSizeToClient(player);
        //#endif
        checkTickRate();
    }

    @Override
    public void onPlayerLoggedOut(ServerPlayer player) {
        IGNYSettings.sprintWhitelistPlayers.remove(player.getUUID());
        checkTickRate();
    }

    public static void onRuleChanged(CarpetRule<?> rule) {
        if (rule.name().equals("betterSprintGameTick")) {
            IGNYSettings.sprintWhitelistPlayers.clear();
            if (IGNYSettings.betterSprintGameTick.equals("playerJoin") && minecraftServer != null) {
                for (ServerPlayer player : minecraftServer.getPlayerList().getPlayers()) {
                    IGNYSettings.sprintWhitelistPlayers.add(player.getUUID());
                }
            }
            checkTickRate();
        }
    }

    private static void checkTickRate() {
        if (minecraftServer != null) {
            //#if MC >= 12003
            TickRateManager manager = minecraftServer.tickRateManager();
            //#elseif MC > 11904
            //$$ TickRateManager manager = ((MinecraftServerInterface)minecraftServer).getTickRateManager();
            //#endif
            if (!IGNYSettings.betterSprintGameTick.equals("false")) {
                if (!TickUtil.shouldSprint(minecraftServer)) {
                    //#if MC <= 11904
                    //$$ TickSpeed.tickrate(20);
                    //#else
                    manager.setTickRate(20);
                    //#endif
                }
                return;
            }
            //#if MC <= 11904
            //$$ if (TickUtil.shouldSprint(minecraftServer) && TickSpeed.tickrate != IGNYSettings.originalTPS) {
            //#else
            if (TickUtil.shouldSprint(minecraftServer) && manager.tickrate() != IGNYSettings.originalTPS) {
                //#endif
                //#if MC <= 11904
                //$$ TickSpeed.tickrate(IGNYSettings.originalTPS);
                //#else
                manager.setTickRate(IGNYSettings.originalTPS);
                //#endif
            }
        }
    }
}
