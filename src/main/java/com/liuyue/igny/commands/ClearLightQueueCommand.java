package com.liuyue.igny.commands;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.mixins.commands.clearLightQueue.ThreadedLevelLightEngineAccessor;
import com.liuyue.igny.utils.CommandPermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClearLightQueueCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("clearlightqueue")
                        .requires(source -> CommandPermissions.canUseCommand(source, IGNYSettings.commandClearLightQueue))
                        .executes(ClearLightQueueCommand::executeCommand)
        );
    }
    private static int executeCommand(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getPlayer().
                //#if MC <= 11904
                //$$ getLevel();
                //#else
                serverLevel();
                //#endif
        LevelLightEngine engine = level.getChunkSource().getLightEngine();
        if (engine instanceof ThreadedLevelLightEngine) {
            ((ThreadedLevelLightEngineAccessor) engine).getLightTasks().clear();
        }
        return 0;
    }
}
