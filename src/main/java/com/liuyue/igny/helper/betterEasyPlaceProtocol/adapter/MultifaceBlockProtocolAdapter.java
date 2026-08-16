package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.MultiStageBlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultifaceBlockProtocolAdapter implements MultiStageBlockProtocolStateAdapter, BlockProtocolStateAdapter {
    public static final MultifaceBlockProtocolAdapter INSTANCE = new MultifaceBlockProtocolAdapter();

    public MultifaceBlockProtocolAdapter() {
    }

    private static boolean hasDirection(BlockState state, Direction direction) {
        if (!(state.getBlock() instanceof MultifaceBlock)) {
            return false;
        }
        return MultifaceBlock.hasFace(state, direction);
    }

    
    @Override
    public void igny$setLoopCount(LoopContext ctx) {
        List<Integer> requireDirection = new ArrayList<>();
        ctx.data = requireDirection;

        Direction[] directions = Direction.values();
        for (int i = 0; i < directions.length; ++i) {
            Direction direction = directions[i];
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

    
    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int index = extraProtocolValue & 0b0111;
        Direction[] directions = Direction.values();
        if (index >= directions.length) {
            return null;
        }

        Level world = context.getLevel();
        BlockState blockWorldState = world.getBlockState(context.getClickedPos());

        return ((MultifaceBlock) fromState.getBlock()).getStateForPlacement(blockWorldState, world, context.getClickedPos(), directions[index]);
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