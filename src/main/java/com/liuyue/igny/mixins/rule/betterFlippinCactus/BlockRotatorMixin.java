package com.liuyue.igny.mixins.rule.betterFlippinCactus;

import carpet.helpers.BlockRotator;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRotator.class)
public class BlockRotatorMixin {
    @Inject(method = "flipBlock", at = @At(value = "RETURN"), cancellable = true)
    private static void onFlipBlock(BlockState state, Level world, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.betterFlippinCactus && !cir.getReturnValueZ()) {
            BlockState newState = getNextRotationState(state);
            if (newState != null && newState != state) {
                BlockPos pos = hit.getBlockPos();
                world.setBlock(pos, newState, Block.UPDATE_CLIENTS | 1024);
                world.setBlocksDirty(pos, state, newState);
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private static BlockState getNextRotationState(BlockState state) {
        DirectionProperty property = null;

        if (state.hasProperty(BlockStateProperties.FACING)) {
            property = BlockStateProperties.FACING;
        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            property = BlockStateProperties.HORIZONTAL_FACING;
        }

        if (property != null) {
            Direction current = state.getValue(property);
            Direction next = getNextDirection(current, property);

            if (property.getPossibleValues().contains(next)) {
                return state.setValue(property, next);
            }
        }

        if (state.hasProperty(BlockStateProperties.AXIS)) {
            Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
            Direction.Axis nextAxis = switch (axis) {
                case X -> Direction.Axis.Z;
                case Z -> Direction.Axis.Y;
                case Y -> Direction.Axis.X;
            };
            return state.setValue(BlockStateProperties.AXIS, nextAxis);
        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            Direction.Axis currentAxis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            return state.setValue(BlockStateProperties.HORIZONTAL_AXIS,
                    currentAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
        }

        return null;
    }

    @Unique
    private static Direction getNextDirection(Direction current, DirectionProperty property) {
        return switch (current) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> property.getPossibleValues().contains(Direction.UP) ? Direction.UP : Direction.NORTH;
            case UP -> Direction.DOWN;
            case DOWN -> Direction.NORTH;
        };
    }
}
