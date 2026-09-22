package com.liuyue.igny.mixins.rule.bonemealableAmethyst;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
//?>= 26.3 ? import net.minecraft.world.level.block.BonemealSource;

@Mixin(BuddingAmethystBlock.class)
public abstract class BuddingAmethystBlockMixin implements BonemealableBlock {
    @Unique ThreadLocal<Boolean> IS_BONEMEAL = ThreadLocal.withInitial(() -> false);

    @Shadow
    protected abstract void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);

    @Override
    //#if >= 26.3
    //$$ public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source)
    //#elseif >= 1.20.2
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state)
    //#else
    //$$ public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient)
    //#endif
    {
        if (!IGNYSettings.BONEMEALABLE_AMETHYST.value()) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            BlockPos targetPos = pos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);
            if (BuddingAmethystBlock.canClusterGrowAtState(targetState)) {
                return true;
            }
            if (targetState.is(Blocks.SMALL_AMETHYST_BUD)
                            && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
                return true;
            }
            if (targetState.is(Blocks.MEDIUM_AMETHYST_BUD)
                            && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
                return true;
            }
            if (targetState.is(Blocks.LARGE_AMETHYST_BUD)
                    && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) //#replace >= 26.3 ? public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source)
    {
        return IGNYSettings.BONEMEALABLE_AMETHYST.value();
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) //#replace >= 26.3 ? public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source)
    {
        if (IGNYSettings.BONEMEALABLE_AMETHYST.value()) {
            try {
                IS_BONEMEAL.set(true);
                this.randomTick(state, level, pos, random);
            } finally {
                IS_BONEMEAL.set(false);
            }
        }
    }

    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", ordinal = 0))
    private int nextInt(RandomSource instance, int i, Operation<Integer> original) {
        return IS_BONEMEAL.get() ? 0 : original.call(instance, i);
    }
}
