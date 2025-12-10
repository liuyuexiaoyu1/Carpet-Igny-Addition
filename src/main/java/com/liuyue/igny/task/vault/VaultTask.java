package com.liuyue.igny.task.vault;

import carpet.helpers.EntityPlayerActionPack;
import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.task.ITask;
import com.liuyue.igny.task.TaskManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VaultTask implements ITask {

    private static final Map<String, VaultTask> INSTANCE_CACHE = new ConcurrentHashMap<>();
    private final MinecraftServer server;
    private final String playerName;
    private final int maxCycles;

    private int currentCycle = 0;
    private int stageTickCounter = 0;
    private ServerPlayer currentFakePlayer = null;
    private Vec3 lastPosition;
    private float lastYaw;
    private float lastPitch;
    private ResourceKey<Level> dimension;
    private boolean isRunning = false;
    private String logoutPlayerName;
    private String pendingFakeName = null;
    private final ServerPlayer operator;
    private boolean paused = false;

    private enum Stage {
        SPAWNING,
        RIGHT_CLICKING,
        WAITING,
        LOGGING_OUT
    }
    private Stage currentStage = Stage.SPAWNING;

    private VaultTask(CommandSourceStack source, String playerName, int maxCycles) {
        this.server = source.getServer();
        this.operator = source.getPlayer();
        this.playerName = playerName;
        this.maxCycles = Math.max(1, maxCycles);
    }

    public static VaultTask getOrCreate(CommandSourceStack source, String playerName, int maxCycles) {
        return INSTANCE_CACHE.computeIfAbsent(playerName, name -> new VaultTask(source, name, maxCycles));
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public String getTaskType() {
        return "Vault";
    }

    @Override
    public Component getStatusText() {
        if (paused) {
            return Component.translatable("igny.task.status.paused")
                    .append(Component.literal(" §7"))
                    .append(Component.translatable("igny.task.status.cycle", currentCycle, maxCycles));
        }
        String status = switch (currentStage) {
            case SPAWNING -> Component.translatable("igny.task.vault.spawning",
                    pendingFakeName != null ? " (" + pendingFakeName + ")" : "").getString();
            case RIGHT_CLICKING -> Component.translatable("igny.task.vault.right_clicking").getString();
            case WAITING -> Component.translatable("igny.task.vault.waiting").getString();
            case LOGGING_OUT -> Component.translatable("igny.task.vault.logging_out").getString();
        };
        return Component.translatable("igny.task.status.cycle", currentCycle, maxCycles)
                .append(Component.literal(" §8| " + status));
    }

    @Override
    public void pause() {
        if (isRunning && !paused) {
            paused = true;
            if (currentFakePlayer instanceof carpet.fakes.ServerPlayerInterface spi) {
                spi.getActionPack().stopAll();
            }
        }
    }

    @Override
    public void resume() {
        if (isRunning && paused) {
            paused = false;
            if (currentStage == Stage.RIGHT_CLICKING) {
                startRightClicking();
            }
        }
    }

    @Override
    public void start() {
        if (isRunning) return;

        ServerPlayer originalPlayer = server.getPlayerList().getPlayerByName(playerName);
        if (originalPlayer == null) {
            sendMessage(Component.translatable("igny.command.playerOperate.vault_fail_offline", playerName), null);
            return;
        }

        if (!(originalPlayer instanceof EntityPlayerMPFake)) {
            sendMessage(Component.translatable("igny.command.playerOperate.vault_fail_not_fake", playerName), null);
            return;
        }

        IGNYSettings.fakePlayerSpawnMemoryLeakFix = true;
        lastPosition = originalPlayer.position();
        lastYaw = originalPlayer.getYRot();
        lastPitch = originalPlayer.getXRot();
        dimension = originalPlayer.level().dimension();

        isRunning = true;
        currentCycle = 0;
        stageTickCounter = 0;
        currentFakePlayer = originalPlayer;
        startRightClicking();
        currentStage = Stage.RIGHT_CLICKING;
        pendingFakeName = null;

        TaskManager.register(this);
    }

    @Override
    public void tick() {
        if (!isRunning || paused) {
            return;
        }

        stageTickCounter++;

        switch (currentStage) {
            case SPAWNING:
                handleSpawning();
                break;
            case RIGHT_CLICKING:
                handleRightClicking();
                break;
            case WAITING:
                handleWaiting();
                break;
            case LOGGING_OUT:
                handleLoggingOut();
                break;
        }
    }

    @Override
    public void stop() {
        if (!isRunning) return;

        isRunning = false;
        cleanupCurrentPlayer();
        pendingFakeName = null;

        currentStage = Stage.SPAWNING;
        stageTickCounter = 0;
        currentCycle = 0;
        TaskManager.unregister(this);
        INSTANCE_CACHE.remove(playerName);
        if (INSTANCE_CACHE.values().stream().allMatch(VaultTask::isStopped)) {
            IGNYSettings.fakePlayerSpawnMemoryLeakFix = false;
        }
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

    private void cleanupCurrentPlayer() {
        if (currentFakePlayer != null) {
            try {
                if (currentFakePlayer instanceof carpet.fakes.ServerPlayerInterface spi) {
                    spi.getActionPack().stopAll();
                }
                if (currentFakePlayer instanceof EntityPlayerMPFake) {
                    currentFakePlayer.connection.onDisconnect(new DisconnectionDetails(Component.literal("Vault cleanup")));
                }
            } catch (Exception e) {
                IGNYServer.LOGGER.error(e.getStackTrace());
            } finally {
                currentFakePlayer = null;
            }
        }
        pendingFakeName = null;
    }

    private void handleSpawning() {
        if (pendingFakeName == null) {
            cleanupCurrentPlayer();

            currentCycle++;
            if (currentCycle > maxCycles) {
                currentCycle = 1;
            }

            pendingFakeName = playerName + "_" + currentCycle;

            try {
                boolean success = EntityPlayerMPFake.createFake(
                        pendingFakeName,
                        server,
                        lastPosition,
                        lastYaw,
                        lastPitch,
                        dimension,
                        GameType.SURVIVAL,
                        false
                );

                if (!success) {
                    stop();
                }
            } catch (Exception e) {
                IGNYServer.LOGGER.error(e.getStackTrace());
                stop();
            }
        } else {
            ServerPlayer candidate = server.getPlayerList().getPlayerByName(pendingFakeName);
            if (candidate != null && candidate.isAlive()) {
                currentFakePlayer = candidate;
                pendingFakeName = null;
                startRightClicking();
                return;
            }

            ServerTickRateManager trm = server.tickRateManager();
            double NANOSECONDS_PER_MILLISECOND = ((double) server.getAverageTickTimeNanos()) / TimeUtil.NANOSECONDS_PER_MILLISECOND;
            double TPS = 1000.0D / Math.max(trm.isSprinting() ? 0.0 : trm.millisecondsPerTick(), NANOSECONDS_PER_MILLISECOND);
            int SPAWN_TIMEOUT_SECONDS = 20;
            if (stageTickCounter >= TPS * SPAWN_TIMEOUT_SECONDS) {
                stop();
                sendMessage(Component.translatable("igny.command.playerOperate.vault_spawn_timeout", playerName, SPAWN_TIMEOUT_SECONDS),
                        "[PlayerOperate] Vault: 玩家 " + playerName + " 无法在 " + SPAWN_TIMEOUT_SECONDS + " 秒内生成假人，停止任务");
            }
        }
    }

    private void startRightClicking() {
        if (currentFakePlayer != null && currentFakePlayer.isAlive()) {
            try {
                if (currentFakePlayer instanceof carpet.fakes.ServerPlayerInterface spi) {
                    EntityPlayerActionPack actionPack = spi.getActionPack();
                    actionPack.stopAll();
                    actionPack.start(EntityPlayerActionPack.ActionType.USE,
                            EntityPlayerActionPack.Action.continuous());

                    currentStage = Stage.RIGHT_CLICKING;
                    stageTickCounter = 0;
                }
            } catch (Exception e) {
                IGNYServer.LOGGER.error(e.getStackTrace());
                currentStage = Stage.LOGGING_OUT;
                stageTickCounter = 0;
            }
        } else {
            currentStage = Stage.SPAWNING;
            stageTickCounter = 0;
            pendingFakeName = null;
        }
    }

    private void handleRightClicking() {
        if (currentFakePlayer == null || !currentFakePlayer.isAlive()) {
            currentStage = Stage.SPAWNING;
            stageTickCounter = 0;
            pendingFakeName = null;
            return;
        }

        if (stageTickCounter >= 100) {
            currentStage = Stage.LOGGING_OUT;
            stageTickCounter = 0;
        }
    }

    private void handleLoggingOut() {
        if (currentFakePlayer != null) {
            logoutPlayerName = currentFakePlayer.getGameProfile()
                    //#if MC>=12109
                    //$$ .name();
                    //#else
                    .getName();
            //#endif

            if (currentFakePlayer instanceof carpet.fakes.ServerPlayerInterface spi) {
                spi.getActionPack().start(EntityPlayerActionPack.ActionType.USE, null);
            }
            if (currentFakePlayer instanceof EntityPlayerMPFake) {
                currentFakePlayer.connection.onDisconnect(new DisconnectionDetails(Component.literal("Vault cycle completed")));
            }
            currentFakePlayer = null;
        }

        currentStage = Stage.WAITING;
        stageTickCounter = 0;
    }

    private void handleWaiting() {
        if (stageTickCounter >= 21) {
            currentStage = Stage.SPAWNING;
            stageTickCounter = 0;
            pendingFakeName = null;
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

    public String getPendingFakeName() {
        return pendingFakeName;
    }

    public ServerPlayer getCurrentFakePlayer() {
        return currentFakePlayer;
    }

    public String getLogoutPlayerName() {
        return logoutPlayerName;
    }
}