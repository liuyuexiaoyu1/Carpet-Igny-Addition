package com.liuyue.igny.mixins.rule.wetSpongeCanAbsorbLava;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
//?>= 1.21.2 ? import net.minecraft.world.level.redstone.Orientation;

//#if > 1.20.1
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
//#endif
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if <= 1.19.4
/*$$import net.minecraft.util.Tuple;
import net.minecraft.world.level.material.Material;
import com.google.common.collect.Lists;
import java.util.Queue;$$*/
//#endif

@Mixin(WetSpongeBlock.class)
public abstract class WetSpongeBlockMixin extends Block {
    public WetSpongeBlockMixin(Properties properties) {
        super(properties);
    }

    //#if > 1.19.4
    @Unique
    private static final Direction[] ALL_DIRECTIONS = Direction.values();
    //#endif

    @Inject(method = "onPlace", at = @At(value = "HEAD"))
    private void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
        if (!oldState.is(state.getBlock())) {
            this.tryAbsorbWater(level, pos);
        }
    }

    @Override
    //?<= 1.20.5 ? @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block,
                                BlockPos blockPos2, //#replace >= 1.21.2 ? Orientation orientation,
            boolean bl) {
        this.tryAbsorbWater(level, blockPos);
        super.neighborChanged(blockState, level, blockPos, block, blockPos2, bl); //#replace >= 1.21.2 ? super.neighborChanged(blockState, level, blockPos, block, orientation, bl);
    }

    @Unique
    private void tryAbsorbWater(Level level, BlockPos blockPos) {
        if (removeFluidBreadthFirstSearch(level, blockPos)) {
            level.setBlock(blockPos, Blocks.SPONGE.defaultBlockState(), 2);
            level.playSound(null, blockPos, SoundEvents.SPONGE_ABSORB, SoundSource.BLOCKS, 1.0F, 1.0F); //#replace <= 1.20.1 ? level.levelEvent(2001, blockPos, Block.getId(Blocks.WATER.defaultBlockState()));
        }
    }

    @Unique
    private boolean removeFluidBreadthFirstSearch(Level level, BlockPos blockPos) {
        //#if > 1.19.4
        return BlockPos.breadthFirstTraversal(
                blockPos,
                6,
                65,
                (blockPosx, consumer) -> {
                    for (Direction direction : ALL_DIRECTIONS) {
                        consumer.accept(blockPosx.relative(direction));
                    }
                },
                blockPos2 -> {
                    if (blockPos2.equals(blockPos)) {
                        return true; //#replace >= 1.21.4 ? return BlockPos.TraversalNodeStatus.ACCEPT;
                    } else {
                        BlockState blockState = level.getBlockState(blockPos2);
                        FluidState fluidState = level.getFluidState(blockPos2);
                        if (!shouldAbsorb(fluidState)) {
                            return false; //#replace >= 1.21.4 ? return BlockPos.TraversalNodeStatus.SKIP;
                        } else if (blockState.getBlock() instanceof BucketPickup bucketPickup
                                && !bucketPickup.pickupBlock(
                                        null, //?> 1.20.1
                                level, blockPos2, blockState).isEmpty()) {
                            return true; //#replace >= 1.21.4 ? return BlockPos.TraversalNodeStatus.ACCEPT;
                        } else {
                            if (blockState.getBlock() instanceof LiquidBlock) {
                                level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
                            } else {
                                if (!blockState.is(Blocks.KELP)
                                        && !blockState.is(Blocks.KELP_PLANT)
                                        && !blockState.is(Blocks.SEAGRASS)
                                        && !blockState.is(Blocks.TALL_SEAGRASS)) {
                                    return false; //#replace >= 1.21.4 ? return BlockPos.TraversalNodeStatus.SKIP;
                                }

                                BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos2) : null;
                                dropResources(blockState, level, blockPos2, blockEntity);
                                level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
                            }
                            return true; //#replace >= 1.21.4 ? return BlockPos.TraversalNodeStatus.ACCEPT;
                        }
                    }
                }
        ) > 1;
        //#else
        /*$$Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<>(blockPos, 0));
        int i = 0;
        while (!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockPos2 = tuple.getA();
            int j = tuple.getB();
            for (Direction direction : Direction.values()) {
                BlockPos blockPos3 = blockPos2.relative(direction);
                BlockState blockState = level.getBlockState(blockPos3);
                FluidState fluidState = level.getFluidState(blockPos3);
                Material material = blockState.getMaterial();
                if (!shouldAbsorb(fluidState)) {
                    if (blockState.getBlock() instanceof BucketPickup && !((BucketPickup)blockState.getBlock()).pickupBlock(level, blockPos3, blockState).isEmpty()) {
                        i++;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockPos3, j + 1));
                        }
                    } else if (blockState.getBlock() instanceof LiquidBlock) {
                        level.setBlock(blockPos3, Blocks.AIR.defaultBlockState(), 3);
                        i++;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockPos3, j + 1));
                        }
                    } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                        BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos3) : null;
                        dropResources(blockState, level, blockPos3, blockEntity);
                        level.setBlock(blockPos3, Blocks.AIR.defaultBlockState(), 3);
                        i++;
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
        return i > 0;$$*/
        //#endif
    }

    @Unique
    private boolean shouldAbsorb(FluidState fluidState) {
        return fluidState.is(FluidTags.LAVA) && IGNYSettings.WET_SPONGE_CAN_ABSORB_LAVA.value();
    }
}
