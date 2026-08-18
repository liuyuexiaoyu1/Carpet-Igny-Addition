package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin extends FaceAttachedHorizontalDirectionalBlock {
    protected LeverBlockMixin(net.minecraft.world.level.block.state.BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!BetterEasyPlaceProtocolHandler.isEasyPlaceState()) {
            super.onPlace(blockState, level, blockPos, blockState2, bl);
            return;
        }
        if (!level.isClientSide() && blockState.getValue(LeverBlock.POWERED)) {
            Direction connected = getDirection(blockState);
            Direction direction = connected.getOpposite();
            //#if MC >= 12105
            //$$ net.minecraft.world.level.redstone.Orientation orientation =
            //$$         net.minecraft.world.level.redstone.ExperimentalRedstoneUtils.initialOrientation(
            //$$                 level, direction, direction.getAxis().isHorizontal() ? Direction.UP : blockState.getValue(FACING));
            //$$ level.updateNeighborsAt(blockPos, (LeverBlock) (Object) this, orientation);
            //$$ level.updateNeighborsAt(blockPos.relative(direction), (LeverBlock) (Object) this, orientation);
            //#else
            level.updateNeighborsAt(blockPos, (LeverBlock) (Object) this);
            level.updateNeighborsAt(blockPos.relative(direction), (LeverBlock) (Object) this);
            //#endif
        }
        super.onPlace(blockState, level, blockPos, blockState2, bl);
    }

    @Unique
    private static Direction getDirection(BlockState state) {
        return switch (state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE)) {
            case FLOOR -> Direction.DOWN;
            case CEILING -> Direction.UP;
            case WALL -> state.getValue(FACING);
        };
    }
}