package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.MultiStageBlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeaPickleBlockProtocolAdapter implements MultiStageBlockProtocolStateAdapter, BlockProtocolStateAdapter {
    public static final SeaPickleBlockProtocolAdapter INSTANCE = new SeaPickleBlockProtocolAdapter();

    public SeaPickleBlockProtocolAdapter() {
    }

    @Override
    public void igny$setLoopCount(LoopContext ctx) {
        boolean isSeaPickle = ctx.stateClient.getBlock() instanceof SeaPickleBlock;
        int curPickles = isSeaPickle ? ctx.stateClient.getValue(SeaPickleBlock.PICKLES) : 0;
        int targetPickles = ctx.stateSchematic.getValue(SeaPickleBlock.PICKLES);

        if (targetPickles > curPickles) {
            ctx.loopCount = targetPickles - curPickles;
        } else {
            ctx.loopCount = 0;
        }
    }

    @Override
    public int igny$toProtocolValueLoop(LoopContext ctx) {
        return (ctx.stateSchematic.getValue(SeaPickleBlock.PICKLES) - 1) & 0b0011;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int maxPickles = (extraProtocolValue & 0b0011) + 1;
        if (fromState.getValue(SeaPickleBlock.PICKLES) > maxPickles) {
            return null;
        }
        return fromState;
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        return 0;
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}