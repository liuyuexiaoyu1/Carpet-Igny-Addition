package com.liuyue.igny.task.rotation;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.task.ITask;
import com.liuyue.igny.task.TaskManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RotationTask implements ITask {

    private static final Map<String, RotationTask> INSTANCE_CACHE = new ConcurrentHashMap<>();
    private final MinecraftServer server;
    private final String playerName;
    private final ServerPlayer operator;

    private final int intervalTicks; // 每 x 刻
    private final float rotationAngle; // 转动 y 度
    private final int totalCycles = -1;

    private ServerPlayer targetPlayer = null;
    private boolean isRunning = false;
    private boolean paused = false;
    private int tickCounter = 0;
    private int currentCycle = 0;

    private RotationTask(CommandSourceStack source, String playerName, int intervalTicks, float rotationAngle) {
        this.server = source.getServer();
        this.operator = source.getPlayer();
        this.playerName = playerName;
        this.intervalTicks = Math.max(1, intervalTicks);
        this.rotationAngle = rotationAngle;
    }

    public static RotationTask getOrCreate(CommandSourceStack source, String playerName, int intervalTicks, float rotationAngle) {
        RotationTask existing = INSTANCE_CACHE.get(playerName);
        if (existing != null && !existing.isStopped()) {
            existing.stop();
        }
        RotationTask newTask = new RotationTask(source, playerName, intervalTicks, rotationAngle);
        INSTANCE_CACHE.put(playerName, newTask);
        return newTask;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public String getTaskType() {
        return "Rotation";
    }

    @Override
    public Component getStatusText() {
        String cycleInfo = totalCycles == -1 ? "Infinite" : currentCycle + "/" + totalCycles;
        if (paused) {
            return Component.literal("§7Rotation §8| §cPaused §8| §f" + cycleInfo + " §7Next: §f" + (intervalTicks - tickCounter) + "t");
        }
        return Component.literal("§7Rotation §8| §aRunning §8| §f" + cycleInfo + " §8| §7Interval: §f" + intervalTicks + "t §7Angle: §f" + rotationAngle + "°");
    }

    @Override
    public void start() {
        if (isRunning) return;

        ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
        if (!(player instanceof EntityPlayerMPFake)) {
            return;
        }

        this.targetPlayer = player;
        this.isRunning = true;
        this.tickCounter = 0;
        this.currentCycle = 0;
        TaskManager.register(this);
    }

    @Override
    public void tick() {
        if (!isRunning || paused) return;

        if (targetPlayer == null || !targetPlayer.isAlive() || targetPlayer.hasDisconnected()) {
            stop();
            return;
        }

        tickCounter++;

        if (tickCounter >= intervalTicks) {
            // 执行旋转
            float newYaw = targetPlayer.getYRot() + rotationAngle;
            targetPlayer.setYRot(newYaw);


            tickCounter = 0;
            currentCycle++;

            if (totalCycles != -1 && currentCycle >= totalCycles) {
                stop();
            }
        }
    }

    @Override
    public void pause() {
        if (isRunning) paused = true;
    }

    @Override
    public void resume() {
        if (isRunning) paused = false;
    }

    @Override
    public void stop() {
        if (!isRunning) return;
        isRunning = false;
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
}