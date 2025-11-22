package com.liuyue.igny.mixins.rule.SpongeCanAbsorbLava;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.*;

import java.util.Objects;

import static net.minecraft.world.level.block.Block.dropResources;

@Mixin(SpongeBlock.class)
public class SpongeBlockMixin {
    @Shadow @Final private static Direction[] ALL_DIRECTIONS;



    /**
    * @author Liuyue_awa
    * @reason 让海绵吸收岩浆
    */
   @Overwrite
   private boolean removeWaterBreadthFirstSearch(Level level, BlockPos blockPos) {
       return BlockPos.breadthFirstTraversal(blockPos, 6, 65, (blockPosx, consumer) -> {
           for(Direction direction : ALL_DIRECTIONS) {
               consumer.accept(blockPosx.relative(direction));
           }

       }, (blockPos2) -> {
           if (blockPos2.equals(blockPos)) {
               //#if MC>=12104
               //$$ return BlockPos.TraversalNodeStatus.ACCEPT;
               //#else
               return true;
               //#endif
           } else {
               BlockState blockState = level.getBlockState(blockPos2);
               FluidState fluidState = level.getFluidState(blockPos2);
               if (!shouldAbsorb(fluidState)) {
                   //#if MC>=12104
                   //$$ return BlockPos.TraversalNodeStatus.SKIP;
                   //#else
                   return false;
                   //#endif
               } else {
                   Block block = blockState.getBlock();
                   if (block instanceof BucketPickup) {
                       BucketPickup bucketPickup = (BucketPickup)block;
                       if (!bucketPickup.pickupBlock((Player)null, level, blockPos2, blockState).isEmpty()) {
                           //#if MC>=12104
                           //$$ return BlockPos.TraversalNodeStatus.ACCEPT;
                           //#else
                           return true;
                           //#endif
                       }
                   }

                   if (blockState.getBlock() instanceof LiquidBlock) {
                       level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
                   } else {
                       if (!blockState.is(Blocks.KELP) && !blockState.is(Blocks.KELP_PLANT) && !blockState.is(Blocks.SEAGRASS) && !blockState.is(Blocks.TALL_SEAGRASS)) {
                           //#if MC>=12104
                           //$$ return BlockPos.TraversalNodeStatus.SKIP;
                           //#else
                           return false;
                           //#endif
                       }

                       BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos2) : null;
                       dropResources(blockState, level, blockPos2, blockEntity);
                       level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
                   }
                   //#if MC>=12104
                   //$$ return BlockPos.TraversalNodeStatus.ACCEPT;
                   //#else
                   return true;
                   //#endif
               }
           }
       }) > 1;
   }
   @Unique
    private boolean shouldAbsorb(FluidState fluidState){
       if (fluidState.is(FluidTags.WATER)){
           return true;
       }else return fluidState.is(FluidTags.LAVA) && Objects.equals(IGNYSettings.SpongeCanAbsorbLava, "true");
   }

}
