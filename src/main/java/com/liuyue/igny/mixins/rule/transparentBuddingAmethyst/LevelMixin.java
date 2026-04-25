package com.liuyue.igny.mixins.rule.transparentBuddingAmethyst;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.manager.AmethystVaultManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

@Mixin(Level.class)
public class LevelMixin {

    @Unique
    private static final Map<BlockPos, Integer> RETRY_QUEUES = new ConcurrentHashMap<>();
    @Unique
    private static final Random RANDOM = new Random();

    @Unique
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1, r -> {
        Thread t = new Thread(r, "Amethyst-Restore-Scheduler");
        t.setDaemon(true);
        return t;
    });

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At(value = "HEAD"))
    private void setBlockHead(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.transparentBuddingAmethyst) {
            Level level = (Level) (Object) this;
            if (level.isClientSide()) return;

            BlockState oldState = level.getBlockState(pos);
            if (oldState.is(Blocks.BUDDING_AMETHYST) && IGNYSettings.movingBlocks.get() && !state.is(Blocks.BUDDING_AMETHYST)) {
                AmethystVaultManager.INSTANCE.storeBud(pos, oldState);
            }
        }
    }

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("RETURN"))
    private void setBlockReturn(BlockPos pos, BlockState newState, int flags, int recursion, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.transparentBuddingAmethyst) {
            Level level = (Level) (Object) this;
            if (level.isClientSide() || !cir.getReturnValue()) return;

            if (newState.isAir()) {
                if (AmethystVaultManager.INSTANCE.has(pos)) {
                    scheduleRestore(level, pos.immutable());
                }
            } else if (newState.is(Blocks.PISTON) || newState.is(Blocks.STICKY_PISTON)) {
                Direction facing = newState.getValue(PistonBaseBlock.FACING);
                if (!newState.getValue(PistonBaseBlock.EXTENDED)) {
                    BlockPos targetPos = pos.relative(facing);
                    if (AmethystVaultManager.INSTANCE.has(targetPos)) {
                        scheduleRestore(level, targetPos.immutable());
                    }
                }
            }
        }
    }

    @Unique
    private void scheduleRestore(Level level, BlockPos pos) {
        MinecraftServer server = level.getServer();
        if (server == null) return;
        int currentTry = RETRY_QUEUES.getOrDefault(pos, 0);
        if (currentTry >= 4) {
            RETRY_QUEUES.put(pos, 0);
        }

        startSamplingCycle(level, server, pos, 0);
    }

    @Unique
    private void startSamplingCycle(Level level, MinecraftServer server, BlockPos pos, int tryCount) {
        if (tryCount >= 4) {
            RETRY_QUEUES.put(pos, 4);
            return;
        }

        SCHEDULER.schedule(() -> server.execute(() -> {
            if (!isSafe(level, pos)) {
                retryLater(level, server, pos, tryCount);
                return;
            }

            SCHEDULER.schedule(() -> server.execute(() -> {
                if (isSafe(level, pos)) {
                    BlockState savedState = AmethystVaultManager.INSTANCE.getAndRemove(pos);
                    if (savedState != null) {
                        RETRY_QUEUES.remove(pos);
                        level.setBlock(pos, savedState, 130);
                    }
                } else {
                    retryLater(level, server, pos, tryCount);
                }
            }), 100 + RANDOM.nextInt(200), TimeUnit.MILLISECONDS);

        }), 50 + RANDOM.nextInt(150), TimeUnit.MILLISECONDS);
    }

    @Unique
    private void retryLater(Level level, MinecraftServer server, BlockPos pos, int tryCount) {
        SCHEDULER.schedule(() -> startSamplingCycle(level, server, pos, tryCount + 1), 2, TimeUnit.SECONDS);
    }

    @Unique
    private boolean isSafe(Level level, BlockPos pos) {
        if (!level.getBlockState(pos).isAir()) return false;
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).is(Blocks.MOVING_PISTON)) return false;
        }
        return true;
    }

    @Mixin(BlockBehaviour.BlockStateBase.class)
    abstract static class BlockStateBaseMixin {
        @Inject(method = "getPistonPushReaction", at = @At("HEAD"), cancellable = true)
        private void getPistonPushReaction(CallbackInfoReturnable<PushReaction> cir) {
            if (IGNYSettings.transparentBuddingAmethyst) {
                BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;
                if (state.is(Blocks.BUDDING_AMETHYST)) {
                    cir.setReturnValue(PushReaction.DESTROY);
                }
            }
        }

        @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("HEAD"), cancellable = true)
        private void getCollisionShape(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
            if (IGNYSettings.transparentBuddingAmethyst) {
                BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;
                if (state.is(Blocks.BUDDING_AMETHYST)) {
                    if (context instanceof EntityCollisionContext entityContext) {
                        if (entityContext.getEntity() instanceof LivingEntity) {
                            cir.setReturnValue(Shapes.empty());
                        }
                    }
                }
            }
        }

        @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
        private void getDestroySpeed(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
            if (IGNYSettings.transparentBuddingAmethyst) {
                BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;
                if (state.is(Blocks.BUDDING_AMETHYST)) {
                    cir.setReturnValue(-1.0F);
                }
            }
        }
    }
}