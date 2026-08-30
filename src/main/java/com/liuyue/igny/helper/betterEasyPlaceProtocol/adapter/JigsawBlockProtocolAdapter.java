package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JigsawBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final JigsawBlockProtocolAdapter INSTANCE = new JigsawBlockProtocolAdapter();

    public JigsawBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int orientationOrdinal = fromState.getValue(JigsawBlock.ORIENTATION).ordinal();
        return protocolValue | ((orientationOrdinal + 1) & 0b0000_1111);
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int orientationOrdinal = Math.floorMod((extraProtocolValue & 0b0000_1111) - 1, 12);
        return fromState.setValue(JigsawBlock.ORIENTATION, FrontAndTop.values()[orientationOrdinal]);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}