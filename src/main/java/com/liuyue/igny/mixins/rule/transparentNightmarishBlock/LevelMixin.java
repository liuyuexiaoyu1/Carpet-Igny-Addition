package com.liuyue.igny.mixins.rule.transparentNightmarishBlock;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.manager.BlockVaultManager;
import com.liuyue.igny.utils.RuleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystClusterBlock;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(Level.class)
public abstract class LevelMixin {

    @Unique
    private final Map<BlockPos, Integer> igny$restoreTimers = new HashMap<>();
    @Unique
    private final Map<BlockPos, BlockState[]> igny$neighborSnapshots = new HashMap<>();
    @Unique
    private boolean igny$initialized = false;

    @Inject(method = "tickBlockEntities", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Level level = (Level) (Object) this;
        if (level.isClientSide()) return;

        if (!igny$initialized) {
            String currentDim = level.dimension().location().toString();
            for (String key : BlockVaultManager.INSTANCE.getPendingRestore()) {
                if (key.startsWith(currentDim + ":")) {
                    long posLong = Long.parseLong(key.substring(currentDim.length() + 1));
                    igny$restoreTimers.putIfAbsent(BlockPos.of(posLong).immutable(), 20);
                }
            }
            igny$initialized = true;
        }

        if (igny$restoreTimers.isEmpty()) return;

        Iterator<Map.Entry<BlockPos, Integer>> it = igny$restoreTimers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Integer> entry = it.next();
            BlockPos pos = entry.getKey();
            int attemptCount = entry.getValue();

            if (attemptCount <= 0) {
                BlockVaultManager.INSTANCE.markPending(level, pos);
                igny$neighborSnapshots.remove(pos);
                it.remove();
                continue;
            }

            boolean isAir = level.getBlockState(pos).isAir();
            boolean changed = false;
            BlockState[] lastStates = igny$neighborSnapshots.computeIfAbsent(pos, k -> new BlockState[6]);
            Direction[] dirs = Direction.values();

            for (int i = 0; i < 6; i++) {
                BlockState currentState = level.getBlockState(pos.relative(dirs[i]));
                if (lastStates[i] == null || !lastStates[i].equals(currentState)) {
                    lastStates[i] = currentState;
                    changed = true;
                }
            }

            if (isAir && !changed) {
                BlockVaultManager.INSTANCE.restoreBlock(level, pos);
                igny$neighborSnapshots.remove(pos);
                it.remove();
            } else {
                entry.setValue(attemptCount - 1);
            }
        }
    }

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At(value = "HEAD"))
    private void setBlockHead(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.TRANSPARENT_NIGHTMARISH_BLOCK.value()) {
            Level level = (Level) (Object) this;
            if (level.isClientSide()) return;

            BlockState oldState = level.getBlockState(pos);
            if (RuleUtil.isNightmarishBlock(oldState.getBlock()) && IGNYSettings.movingBlocks.get() && !RuleUtil.isNightmarishBlock(state.getBlock())) {
                BlockVaultManager.INSTANCE.storeBlock(level, pos, oldState);
            }
        }
    }

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("RETURN"))
    private void setBlockReturn(BlockPos pos, BlockState newState, int flags, int recursion, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level) (Object) this;
        if (level.isClientSide() || !cir.getReturnValue()) return;
        if (newState.isAir() && BlockVaultManager.INSTANCE.has(level, pos)) {
            igny$restoreTimers.put(pos.immutable(), 20);
            igny$neighborSnapshots.remove(pos);
        }
    }

    @Mixin(BlockBehaviour.BlockStateBase.class)
    abstract static class BlockStateBaseMixin {
        @Inject(method = "getPistonPushReaction", at = @At("HEAD"), cancellable = true)
        private void getPistonPushReaction(CallbackInfoReturnable<PushReaction> cir) {
            if (IGNYSettings.TRANSPARENT_NIGHTMARISH_BLOCK.value()) {
                BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;
                if (state instanceof BlockState blockState) {
                    if (RuleUtil.isNightmarishBlock(blockState.getBlock())) {
                        cir.setReturnValue(PushReaction.DESTROY);
                    }
                }
            }
        }

        @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("HEAD"), cancellable = true)
        private void getCollisionShape(net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
            if (IGNYSettings.TRANSPARENT_NIGHTMARISH_BLOCK.value()) {
                BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;
                if (state instanceof BlockState blockState) {
                    if (RuleUtil.isNightmarishBlock(blockState.getBlock()) || state.getBlock() instanceof AmethystClusterBlock){
                        if (context instanceof EntityCollisionContext ecc && !(ecc.getEntity() instanceof net.minecraft.world.entity.player.Player)) {
                            cir.setReturnValue(Shapes.empty());
                        }
                    }
                }
            }
        }

        @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
        private void getDestroySpeed(net.minecraft.world.level.BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
            if (IGNYSettings.TRANSPARENT_NIGHTMARISH_BLOCK.value()) {
                BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;
                if (state instanceof BlockState blockState) {
                    if (RuleUtil.isNightmarishBlock(blockState.getBlock())) {
                        cir.setReturnValue(-1.0F);
                    }
                }
            }
        }
    }
}