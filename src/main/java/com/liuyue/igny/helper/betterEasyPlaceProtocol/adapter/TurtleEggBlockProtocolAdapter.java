package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.MultiStageBlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TurtleEggBlockProtocolAdapter implements MultiStageBlockProtocolStateAdapter, BlockProtocolStateAdapter {
    public static final TurtleEggBlockProtocolAdapter INSTANCE = new TurtleEggBlockProtocolAdapter();

    public TurtleEggBlockProtocolAdapter() {
    }

    @Override
    public void igny$setLoopCount(LoopContext ctx) {
        boolean isTurtleEgg = ctx.stateClient.getBlock() instanceof TurtleEggBlock;
        int curEggs = isTurtleEgg ? ctx.stateClient.getValue(TurtleEggBlock.EGGS) : 0;
        int targetEggs = ctx.stateSchematic.getValue(TurtleEggBlock.EGGS);

        if (targetEggs > curEggs) {
            ctx.loopCount = targetEggs - curEggs;
        } else {
            ctx.loopCount = 0;
        }
    }

    @Override
    public int igny$toProtocolValueLoop(LoopContext ctx) {
        return (ctx.stateSchematic.getValue(TurtleEggBlock.EGGS) - 1) & 0b0011;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int maxEggs = (extraProtocolValue & 0b0011) + 1;
        if (fromState.getValue(TurtleEggBlock.EGGS) > maxEggs) {
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
        return ProtocolType.REPLACE;
    }
}