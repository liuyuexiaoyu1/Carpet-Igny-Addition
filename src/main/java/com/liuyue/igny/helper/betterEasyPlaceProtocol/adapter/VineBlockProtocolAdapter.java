package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.MultiStageBlockProtocolStateAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VineBlockProtocolAdapter implements MultiStageBlockProtocolStateAdapter, BlockProtocolStateAdapter {
    public static final VineBlockProtocolAdapter INSTANCE = new VineBlockProtocolAdapter();

    private static final Direction[] DIRECTIONS =
            Arrays.stream(Direction.values())
                    .filter(d -> d != Direction.DOWN)
                    .toArray(Direction[]::new);

    public VineBlockProtocolAdapter() {
    }

    private static boolean hasDirection(BlockState state, Direction direction) {
        if (!(state.getBlock() instanceof VineBlock)) {
            return false;
        }

        BooleanProperty booleanProperty = VineBlock.getPropertyForFace(direction);
        return state.hasProperty(booleanProperty) && state.getValue(booleanProperty);
    }

    @Override
    public void igny$setLoopCount(LoopContext ctx) {
        List<Integer> requireDirection = new ArrayList<>();
        ctx.data = requireDirection;

        for (int i = 0; i < DIRECTIONS.length; ++i) {
            Direction direction = DIRECTIONS[i];

            if (hasDirection(ctx.stateSchematic, direction) &&
                    !hasDirection(ctx.stateClient, direction)) {
                requireDirection.add(i);
                ++ctx.loopCount;
            }
        }
    }

    @Override
    public int igny$toProtocolValueLoop(LoopContext ctx) {
        @SuppressWarnings("unchecked")
        List<Integer> requireDirection = (List<Integer>) ctx.data;

        if (ctx.loopIndex >= requireDirection.size()) {
            return 0b0111;
        }

        return requireDirection.get(ctx.loopIndex) & 0b0111;
    }

    private static @Nullable BlockState withDirection(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        BooleanProperty property = VineBlock.getPropertyForFace(direction);
        boolean isVine = state.getBlock() instanceof VineBlock;

        if (isVine && state.getValue(property)) {
            return null;
        }

        if (!VineBlock.isAcceptableNeighbour(world, pos.relative(direction), direction)) {
            return null;
        }

        BlockState baseState = isVine ? state : Blocks.VINE.defaultBlockState();
        return baseState.setValue(property, true);
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int index = extraProtocolValue & 0b0111;
        if (index >= DIRECTIONS.length) {
            return null;
        }

        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockWorldState = world.getBlockState(blockPos);

        return withDirection(blockWorldState, world, blockPos, DIRECTIONS[index]);
    }

    
    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        return 0;
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}