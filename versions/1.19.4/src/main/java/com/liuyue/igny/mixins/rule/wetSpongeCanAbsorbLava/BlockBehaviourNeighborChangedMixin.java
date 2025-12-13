package com.liuyue.igny.mixins.rule.wetSpongeCanAbsorbLava;

import com.google.common.collect.Lists;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

import static net.minecraft.world.level.block.Block.dropResources;

@SuppressWarnings("unchecked")
@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourNeighborChangedMixin {

    @Inject(method = "neighborChanged", at = @At("HEAD"))
    private void onNeighborChanged(
            BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl, CallbackInfo ci
    ) {

        if (blockState.getBlock() instanceof WetSpongeBlock) {
            if (removeFluidBreadthFirstSearch(level, blockPos)) {
                level.setBlock(blockPos, Blocks.SPONGE.defaultBlockState(), 2);
                level.levelEvent(2001, blockPos, Block.getId(Blocks.WATER.defaultBlockState()));
            }
        }
    }

    @Unique
    private boolean removeFluidBreadthFirstSearch(Level level, BlockPos blockPos) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<>(blockPos, 0));
        int i = 0;

        while(!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = (Tuple)queue.poll();
            BlockPos blockPos2 = tuple.getA();
            int j = tuple.getB();

            for(Direction direction : Direction.values()) {
                BlockPos blockPos3 = blockPos2.relative(direction);
                BlockState blockState = level.getBlockState(blockPos3);
                FluidState fluidState = level.getFluidState(blockPos3);
                Material material = blockState.getMaterial();
                if (shouldAbsorb(fluidState)) {
                    if (blockState.getBlock() instanceof BucketPickup && !((BucketPickup)blockState.getBlock()).pickupBlock(level, blockPos3, blockState).isEmpty()) {
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockPos3, j + 1));
                        }
                    } else if (blockState.getBlock() instanceof LiquidBlock) {
                        level.setBlock(blockPos3, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockPos3, j + 1));
                        }
                    } else if (material == Material.LAVA || material == Material.REPLACEABLE_WATER_PLANT) {
                        BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos3) : null;
                        dropResources(blockState, level, blockPos3, blockEntity);
                        level.setBlock(blockPos3, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockPos3, j + 1));
                        }
                    }
                }
            }

            if (i > 64) {
                break;
            }
        }

        return i > 0;
    }

    @Unique
    private boolean shouldAbsorb(FluidState fluidState) {
        return fluidState.is(FluidTags.LAVA) && IGNYSettings.wetSpongeCanAbsorbLava;
    }
}
