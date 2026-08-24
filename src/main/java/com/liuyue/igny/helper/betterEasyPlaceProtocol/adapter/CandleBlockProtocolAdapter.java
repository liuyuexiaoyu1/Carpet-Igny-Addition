package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.MultiStageBlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CandleBlockProtocolAdapter implements MultiStageBlockProtocolStateAdapter, BlockProtocolStateAdapter {
    public static final CandleBlockProtocolAdapter INSTANCE = new CandleBlockProtocolAdapter();

    public CandleBlockProtocolAdapter() {
    }

    @Override
    public void igny$setLoopCount(LoopContext ctx) {
        boolean isCandle = ctx.stateClient.getBlock() instanceof CandleBlock;
        int curCandles = isCandle ? ctx.stateClient.getValue(CandleBlock.CANDLES) : 0;
        int targetCandles = ctx.stateSchematic.getValue(CandleBlock.CANDLES);

        if (targetCandles > curCandles) {
            ctx.loopCount = targetCandles - curCandles;
        } else {
            ctx.loopCount = 0;
        }
    }

    @Override
    public int igny$toProtocolValueLoop(LoopContext ctx) {
        int candles = (ctx.stateSchematic.getValue(CandleBlock.CANDLES) - 1) & 0b0011;
        boolean lit = ctx.stateSchematic.getValue(CandleBlock.LIT);
        int litBit = lit ? 0b0100 : 0b0000;
        return candles | litBit;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int candles = (extraProtocolValue & 0b0011) + 1;
        boolean lit = ((extraProtocolValue >> 2) & 0b1) == 0b1;
        if (fromState.getValue(CandleBlock.CANDLES) > candles) {
            return null;
        }
        return fromState.setValue(CandleBlock.CANDLES, candles).setValue(CandleBlock.LIT, lit);
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