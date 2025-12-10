package com.liuyue.igny.task.pressuse;

import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.task.ITask;
import com.liuyue.igny.task.TaskManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PressUseTask implements ITask {

    private static final Map<String, PressUseTask> INSTANCE_CACHE = new ConcurrentHashMap<>();
    private final MinecraftServer server;
    private final String playerName;
    private final int intervalTicks;
    private final int pressDuration;
    private final int totalCycles;

    private ServerPlayer targetPlayer = null;
    private boolean isRunning = false;
    private int tickCounter = 0;
    private int currentCycle = 0;
    private State currentState = State.PRESSING;
    private final ServerPlayer operator;
    private boolean paused = false;

    private enum State {
        PRESSING,
        WAITING
    }

    private PressUseTask(CommandSourceStack source, String playerName, int intervalTicks, int pressDuration, int totalCycles) {
        this.server = source.getServer();
        this.operator = source.getPlayer();
        this.playerName = playerName;
        this.intervalTicks = Math.max(1, intervalTicks);
        this.pressDuration = Math.max(1, pressDuration);
        this.totalCycles = totalCycles;
    }

    public static PressUseTask getOrCreate(CommandSourceStack source, String playerName, int intervalTicks, int pressDuration, int totalCycles) {
        PressUseTask existing = INSTANCE_CACHE.get(playerName);
        if (existing != null && !existing.isStopped()) {
            existing.stop();
        }
        PressUseTask newTask = new PressUseTask(source, playerName, intervalTicks, pressDuration, totalCycles);
        INSTANCE_CACHE.put(playerName, newTask);
        return newTask;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public String getTaskType() {
        return "PressUse";
    }

    @Override
    public Component getStatusText() {
        if (paused) {
            String base = totalCycles == -1 ?
                    Component.translatable("igny.task.status.infinite").getString() :
                    Component.translatable("igny.task.status.cycle", currentCycle, totalCycles).getString();
            return Component.literal(Component.translatable("igny.task.status.paused").getString() + " §7" + base + " §8| §7Press:§f" + pressDuration + "t §7Wait:§f" + intervalTicks + "t");
        }
        String stateStr = switch (currentState) {
            case PRESSING -> Component.translatable("igny.task.status.pressing", tickCounter, pressDuration).getString();
            case WAITING -> Component.translatable("igny.task.status.waiting", tickCounter, intervalTicks).getString();
        };

        String cycleInfo = totalCycles == -1 ?
                Component.translatable("igny.task.status.infinite").getString() :
                Component.translatable("igny.task.status.cycle", currentCycle, totalCycles).getString();

        return Component.literal("§7" + cycleInfo +
                " §8| §7Press:§f" + pressDuration + "t §7Wait:§f" + intervalTicks + "t §8| " + stateStr);
    }

    @Override
    public void pause() {
        if (isRunning && !paused) {
            paused = true;
            stopPressing();
        }
    }

    @Override
    public void resume() {
        if (isRunning && paused) {
            paused = false;
            if (currentState == State.PRESSING) {
                startPressing();
            }
        }
    }

    @Override
    public void stop() {
        if (!isRunning) return;

        isRunning = false;
        stopPressing();
        TaskManager.unregister(this);
        INSTANCE_CACHE.remove(playerName);
    }

    @Override
    public boolean isStopped() {
        return !isRunning;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public MinecraftServer getServer() {
        return server;
    }

    @Override
    public void start() {
        if (isRunning) return;

        ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
        if (player == null) {
            sendMessage(Component.translatable("igny.command.playerOperate.pressuse_fail_offline", playerName), null);
            return;
        }

        if (!(player instanceof EntityPlayerMPFake)) {
            sendMessage(Component.translatable("igny.command.playerOperate.pressuse_fail_not_fake", playerName), null);
            return;
        }

        this.targetPlayer = player;
        this.isRunning = true;
        this.tickCounter = 0;
        this.currentCycle = 0;
        this.currentState = State.PRESSING;

        startPressing();
        TaskManager.register(this);

        String cyclesInfo = (totalCycles == -1) ?
                Component.translatable("igny.task.status.infinite").getString() :
                "×" + totalCycles;
        sendMessage(Component.translatable("igny.command.playerOperate.pressuse_started",
                        playerName, pressDuration, intervalTicks, cyclesInfo),
                "[PlayerOperate] PressUse: Started " + playerName +
                        " (Press=" + pressDuration + "t, Wait=" + intervalTicks + "t, Cycles=" + (totalCycles == -1 ? "∞" : totalCycles) + ")");
    }

    private void startPressing() {
        if (targetPlayer != null && targetPlayer.isAlive()) {
            try {
                if (targetPlayer instanceof ServerPlayerInterface spi) {
                    EntityPlayerActionPack actionPack = spi.getActionPack();
                    actionPack.stopAll();
                    actionPack.start(EntityPlayerActionPack.ActionType.USE,
                            EntityPlayerActionPack.Action.continuous());
                }
            } catch (Exception e) {
                IGNYServer.LOGGER.error(e.getStackTrace());
                stop();
            }
        }
    }

    private void stopPressing() {
        if (targetPlayer != null && targetPlayer.isAlive()) {
            try {
                if (targetPlayer instanceof ServerPlayerInterface spi) {
                    spi.getActionPack().stopAll();
                }
            } catch (Exception e) {
                IGNYServer.LOGGER.error(e.getStackTrace());
            }
        }
    }

    @Override
    public void tick() {
        if (!isRunning || paused) return;

        tickCounter++;

        switch (currentState) {
            case PRESSING:
                if (tickCounter >= pressDuration) {
                    stopPressing();
                    currentCycle++;

                    if (totalCycles != -1 && currentCycle >= totalCycles) {
                        stop();
                        return;
                    }

                    tickCounter = 0;
                    currentState = State.WAITING;
                }
                break;

            case WAITING:
                if (tickCounter >= intervalTicks) {
                    tickCounter = 0;
                    currentState = State.PRESSING;
                    startPressing();
                }
                break;
        }
    }

    private void sendMessage(Component message, @Nullable String consoleMessage) {
        if (operator != null && operator.isAlive()) {
            this.operator.sendSystemMessage(message);
        }
        if (consoleMessage != null) {
            IGNYServer.LOGGER.info(consoleMessage);
        }
    }
}