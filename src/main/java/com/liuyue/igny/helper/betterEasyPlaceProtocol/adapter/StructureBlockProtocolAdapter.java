package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructureBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final StructureBlockProtocolAdapter INSTANCE = new StructureBlockProtocolAdapter();

    public StructureBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int modeOrdinal = fromState.getValue(StructureBlock.MODE).ordinal();
        return modeOrdinal & 0b0011;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int modeOrdinal = extraProtocolValue & 0b0011;
        return fromState.setValue(StructureBlock.MODE, StructureMode.values()[modeOrdinal]);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}