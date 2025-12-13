package com.liuyue.igny.commands;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.task.ITask;
import com.liuyue.igny.task.TaskManager;
import com.liuyue.igny.task.pressuse.PressUseTask;
import com.liuyue.igny.task.vault.VaultTask;
import com.liuyue.igny.utils.CommandPermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerOperateCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("playerOperate")
                        .requires(source -> CommandPermissions.canUseCommand(source, IGNYSettings.commandPlayerOperate))
                        .then(
                                Commands.literal("pauseAll")
                                        .executes(PlayerOperateCommand::pauseAllTasks)
                        )
                        .then(
                                Commands.literal("resumeAll")
                                        .executes(PlayerOperateCommand::resumeAllTasks)
                        )
                        .then(
                                Commands.literal("stopAll")
                                        .executes(PlayerOperateCommand::stopAllTasks)
                        )
                        .then(
                                Commands.argument("player", StringArgumentType.string())
                                        .suggests(PlayerOperateCommand::suggestOnlinePlayers)
                                        .then(Commands.literal("task")
                                                //#if MC >= 12003
                                                .then(
                                                        Commands.literal("vault")
                                                                .executes(ctx -> startVaultTask(ctx, 130))
                                                                .then(
                                                                        Commands.argument("maxCycles", IntegerArgumentType.integer())
                                                                                .executes(PlayerOperateCommand::startVaultTaskWithArg)
                                                                )
                                                )
                                                //#endif
                                                .then(
                                                        Commands.literal("pressUse")
                                                                .then(
                                                                        Commands.argument("interval", IntegerArgumentType.integer(1))
                                                                                .then(
                                                                                        Commands.argument("duration", IntegerArgumentType.integer(1))
                                                                                                .executes(ctx -> startPressUseTask(ctx, -1))
                                                                                                .then(
                                                                                                        Commands.argument("cycles", IntegerArgumentType.integer(1))
                                                                                                                .executes(ctx -> {
                                                                                                                    int cycles = IntegerArgumentType.getInteger(ctx, "cycles");
                                                                                                                    return startPressUseTask(ctx, cycles);
                                                                                                                })
                                                                                                )
                                                                                )
                                                                )
                                                )
                                        )
                                        .then(
                                                Commands.literal("stop")
                                                        .executes(PlayerOperateCommand::stopTaskForPlayer)
                                        )
                                        .then(
                                                Commands.literal("pause")
                                                        .executes(PlayerOperateCommand::pauseTaskForPlayer)
                                        )
                                        .then(
                                                Commands.literal("resume")
                                                        .executes(PlayerOperateCommand::resumeTaskForPlayer)
                                        )
                        )
                        .then(
                                Commands.literal("list")
                                        .executes(PlayerOperateCommand::listAllTasks)
                        )
        );
    }

    private static int stopAllTasks(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        List<ITask> tasks = TaskManager.getAllActiveTasks();

        if (tasks.isEmpty()) {
            source.sendFailure(Component.translatable("igny.command.playerOperate.no_tasks"));
            return 0;
        }

        int stoppedCount = 0;

        for (ITask task : tasks) {
            if (!task.isStopped()) {
                task.stop();
                stoppedCount++;
            }
        }

        Component message;
        if (stoppedCount > 0) {
            message = Component.translatable("igny.command.playerOperate.task_stopped", stoppedCount);
        } else {
            message = Component.translatable("igny.command.playerOperate.task_stopped_none");
        }

        source.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                message, false);
        return stoppedCount;
    }

    private static int pauseAllTasks(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        List<ITask> tasks = TaskManager.getAllActiveTasks();

        if (tasks.isEmpty()) {
            source.sendFailure(Component.translatable("igny.command.playerOperate.no_running_tasks"));
            return 0;
        }

        int pausedCount = 0;
        int alreadyPaused = 0;

        for (ITask task : tasks) {
            if (!task.isPaused()) {
                task.pause();
                pausedCount++;
            } else {
                alreadyPaused++;
            }
        }

        Component message;
        if (pausedCount > 0 && alreadyPaused > 0) {
            message = Component.translatable("igny.command.playerOperate.task_paused_partial", pausedCount, alreadyPaused);
        } else if (pausedCount > 0) {
            message = Component.translatable("igny.command.playerOperate.task_paused", pausedCount);
        } else {
            message = Component.translatable("igny.command.playerOperate.task_paused_none");
        }

        source.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                message, false);
        return pausedCount;
    }

    private static int resumeAllTasks(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        List<ITask> tasks = TaskManager.getAllActiveTasks();

        if (tasks.isEmpty()) {
            source.sendFailure(Component.translatable("igny.command.playerOperate.no_running_tasks"));
            return 0;
        }

        int resumedCount = 0;
        int alreadyRunning = 0;

        for (ITask task : tasks) {
            if (task.isPaused()) {
                task.resume();
                resumedCount++;
            } else {
                alreadyRunning++;
            }
        }

        Component message;
        if (resumedCount > 0 && alreadyRunning > 0) {
            message = Component.translatable("igny.command.playerOperate.task_resumed_partial", resumedCount, alreadyRunning);
        } else if (resumedCount > 0) {
            message = Component.translatable("igny.command.playerOperate.task_resumed", resumedCount);
        } else {
            message = Component.translatable("igny.command.playerOperate.task_resumed_none");
        }

        source.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                message, false);
        return resumedCount;
    }

    private static int pauseTaskForPlayer(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        boolean paused = TaskManager.pauseTask(playerName);
        if (paused) {
            source.sendSuccess(
                    //#if MC > 11904
                    () ->
                    //#endif
                            Component.translatable("igny.command.playerOperate.task_paused_success", playerName),
                    false
            );
            return 1;
        } else {
            ITask task = TaskManager.getTask(playerName);
            if (task == null || task.isStopped()) {
                source.sendFailure(Component.translatable("igny.command.playerOperate.task_paused_fail_no_task", playerName));
            } else if (task.isPaused()) {
                source.sendFailure(Component.translatable("igny.command.playerOperate.task_paused_fail_already", playerName));
            }
            return 0;
        }
    }

    private static int resumeTaskForPlayer(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        boolean resumed = TaskManager.resumeTask(playerName);
        if (resumed) {
            source.sendSuccess(
                    //#if MC > 11904
                    () ->
                    //#endif
                            Component.translatable("igny.command.playerOperate.task_resumed_success", playerName),
                    false
            );
            return 1;
        } else {
            ITask task = TaskManager.getTask(playerName);
            if (task == null || task.isStopped()) {
                source.sendFailure(Component.translatable("igny.command.playerOperate.task_resumed_fail_no_task", playerName));
            } else if (!task.isPaused()) {
                source.sendFailure(Component.translatable("igny.command.playerOperate.task_resumed_fail_running", playerName));
            }
            return 0;
        }
    }

    private static int startPressUseTask(CommandContext<CommandSourceStack> context, int cycles) {
        String playerName = StringArgumentType.getString(context, "player");
        int interval = IntegerArgumentType.getInteger(context, "interval");
        int duration = IntegerArgumentType.getInteger(context, "duration");
        CommandSourceStack source = context.getSource();

        ServerPlayer player = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (player == null) {
            source.sendFailure(Component.translatable("igny.command.playerOperate.player_offline", playerName));
            return 0;
        }

        if (!(player instanceof EntityPlayerMPFake)) {
            source.sendFailure(Component.translatable("igny.command.playerOperate.not_fake_player", playerName));
            return 0;
        }

        PressUseTask task = PressUseTask.getOrCreate(source, playerName, interval, duration, cycles);
        task.start();

        String cyclesStr = cycles == -1 ?
                Component.translatable("igny.task.status.infinite").getString() :
                String.valueOf(cycles);

        source.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                        Component.translatable("igny.command.playerOperate.pressuse_started",
                                playerName, duration, interval, cyclesStr),
                false
        );
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestOnlinePlayers(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder builder) {
        CommandSourceStack source = context.getSource();
        return SharedSuggestionProvider.suggest(
                source.getServer().getPlayerList().getPlayers().stream()
                        .map(ServerPlayer::getName)
                        .map(Component::getString),
                builder
        );
    }
    //#if MC >= 12003
    private static int startVaultTaskWithArg(CommandContext<CommandSourceStack> context) {
        int maxCycles = IntegerArgumentType.getInteger(context, "maxCycles");
        return startVaultTask(context, maxCycles);
    }

    private static int startVaultTask(CommandContext<CommandSourceStack> context, int maxCycles) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        ServerPlayer player = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (player == null) {
            source.sendFailure(Component.translatable("igny.command.playerOperate.player_offline", playerName));
            return 0;
        }

        if (!(player instanceof EntityPlayerMPFake)) {
            source.sendFailure(Component.translatable("igny.command.playerOperate.not_fake_player", playerName));
            return 0;
        }

        VaultTask task = VaultTask.getOrCreate(source, playerName, maxCycles);
        task.start();

        source.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                        Component.translatable("igny.command.playerOperate.vault_started", playerName, maxCycles),
                false
        );
        return 1;
    }
    //#endif

    private static int stopTaskForPlayer(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        boolean stopped = TaskManager.stopTask(playerName);
        if (stopped) {
            source.sendSuccess(
                    //#if MC > 11904
                    () ->
                    //#endif
                            Component.translatable("igny.command.playerOperate.task_stopped_success", playerName),
                    false
            );
            return 1;
        } else {
            source.sendFailure(Component.translatable("igny.command.playerOperate.task_stopped_fail", playerName));
            return 0;
        }
    }

    private static int listAllTasks(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        List<ITask> tasks = TaskManager.getAllActiveTasks();

        if (tasks.isEmpty()) {
            source.sendSuccess(
                    //#if MC > 11904
                    () ->
                    //#endif
                            Component.translatable("igny.command.playerOperate.list_no_tasks"),
                    false
            );
            return 1;
        }

        int runningCount = TaskManager.getRunningCount();
        int pausedCount = TaskManager.getPausedCount();

        Component header = Component.translatable("igny.command.playerOperate.list_header",
                        tasks.size(), runningCount, pausedCount)
                .append(Component.literal("\n"))
                .append(createGlobalControlButton(
                        Component.translatable("igny.command.playerOperate.button_pause_all"),
                        "/playerOperate pauseAll",
                        Component.translatable("igny.command.playerOperate.hover_pause_all")))
                .append(Component.literal(" "))
                .append(createGlobalControlButton(
                        Component.translatable("igny.command.playerOperate.button_resume_all"),
                        "/playerOperate resumeAll",
                        Component.translatable("igny.command.playerOperate.hover_resume_all")))
                .append(Component.literal(" "))
                .append(createGlobalControlButton(
                        Component.translatable("igny.command.playerOperate.button_stop_all"),
                        "/playerOperate stopAll",
                        Component.translatable("igny.command.playerOperate.hover_stop_all")))
                .append(Component.literal("\n"));

        source.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                header, false);

        for (ITask task : tasks) {
            String baseName = task.getPlayerName();
            String taskType = task.getTaskType();
            boolean isPaused = task.isPaused();

            Component pauseButton = createTaskButton(
                    Component.translatable("igny.command.playerOperate.button_pause"),
                    "/playerOperate " + baseName + " pause",
                    Component.translatable("igny.command.playerOperate.hover_pause"),
                    isPaused);
            Component resumeButton = createTaskButton(
                    Component.translatable("igny.command.playerOperate.button_resume"),
                    "/playerOperate " + baseName + " resume",
                    Component.translatable("igny.command.playerOperate.hover_resume"),
                    !isPaused);
            Component stopButton = createTaskButton(
                    Component.translatable("igny.command.playerOperate.button_stop"),
                    "/playerOperate " + baseName + " stop",
                    Component.translatable("igny.command.playerOperate.hover_stop"),
                    false);

            String statusIcon = isPaused ? "§8⏸" : "§a▶";
            Component taskInfo = Component.literal(statusIcon + " §f" + baseName)
                    .append(Component.literal(" §6[" + taskType + "] "))
                    .append(task.getStatusText())
                    .append(Component.literal(" "))
                    .append(isPaused ? resumeButton : pauseButton)
                    .append(Component.literal(" "))
                    .append(stopButton);

            source.sendSuccess(
                    //#if MC > 11904
                    () ->
                    //#endif
                    taskInfo, false);
        }

        return 1;
    }

    private static Component createTaskButton(Component text, String command, Component hoverText, boolean disabled) {
        if (disabled) {
            return Component.literal("§8" + text.getString().replaceAll("§[0-9a-f]", ""))
                    .withStyle(style -> style
                                    //#if MC>=12105
                                    //$$ .withItalic(true)
                                    //$$ .withHoverEvent(new HoverEvent.ShowText(
                                    //$$     hoverText.copy()
                                    //$$         .append(Component.literal(" "))
                                    //$$         .append(Component.translatable("igny.command.playerOperate.hover_disabled"))
                                    //$$ ))
                                    //#else
                                    .withItalic(true)
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            hoverText.copy()
                                                    .append(Component.literal(" "))
                                                    .append(Component.translatable("igny.command.playerOperate.hover_disabled"))
                                    ))
                            //#endif
                    );
        } else {
            return text.copy()
                    .withStyle(style -> style
                                    //#if MC>=12105
                                    //$$ .withClickEvent(new ClickEvent.RunCommand(command))
                                    //$$ .withHoverEvent(new HoverEvent.ShowText(hoverText))
                                    //#else
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))
                            //#endif
                    );
        }
    }

    private static Component createGlobalControlButton(Component text, String command, Component hoverText) {
        return text.copy()
                .withStyle(style -> style
                                //#if MC>=12105
                                //$$ .withClickEvent(new ClickEvent.RunCommand(command))
                                //$$ .withHoverEvent(new HoverEvent.ShowText(hoverText))
                                //#else
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))
                        //#endif
                );
    }
}