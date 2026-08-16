package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.MultiStageBlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SnowLayerBlockProtocolAdapter implements MultiStageBlockProtocolStateAdapter, BlockProtocolStateAdapter {
    public static final SnowLayerBlockProtocolAdapter INSTANCE = new SnowLayerBlockProtocolAdapter();

    public SnowLayerBlockProtocolAdapter() {
    }

    @Override
    public void igny$setLoopCount(LoopContext ctx) {
        boolean isSnow = ctx.stateClient.getBlock() instanceof SnowLayerBlock;
        int curLayers = isSnow ? ctx.stateClient.getValue(SnowLayerBlock.LAYERS) : 0;
        int targetLayers = ctx.stateSchematic.getValue(SnowLayerBlock.LAYERS);

        if (targetLayers > curLayers) {
            ctx.loopCount = targetLayers - curLayers;
        } else {
            ctx.loopCount = 0;
        }
    }

    @Override
    public int igny$toProtocolValueLoop(LoopContext ctx) {
        return (ctx.stateSchematic.getValue(SnowLayerBlock.LAYERS) - 1) & 0b0111;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int maxLayers = (extraProtocolValue & 0b0111) + 1;
        if (fromState.getValue(SnowLayerBlock.LAYERS) > maxLayers) {
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