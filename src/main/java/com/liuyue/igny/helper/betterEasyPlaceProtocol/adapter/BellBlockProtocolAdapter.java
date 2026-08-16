package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BellBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final BellBlockProtocolAdapter INSTANCE = new BellBlockProtocolAdapter();

    public BellBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int attachmentOrdinal = fromState.getValue(BellBlock.ATTACHMENT).ordinal();
        int bits = (attachmentOrdinal & 0b0000_0011) << 4;
        return protocolValue | bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int attachmentOrdinal = (extraProtocolValue & 0b0011_0000) >>> 4;
        return fromState.setValue(BellBlock.ATTACHMENT, BellAttachType.values()[attachmentOrdinal]);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}