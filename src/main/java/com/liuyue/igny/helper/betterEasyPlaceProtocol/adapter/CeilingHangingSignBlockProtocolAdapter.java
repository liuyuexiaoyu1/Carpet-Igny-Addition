package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeilingHangingSignBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final CeilingHangingSignBlockProtocolAdapter INSTANCE = new CeilingHangingSignBlockProtocolAdapter();

    public CeilingHangingSignBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int rotation = fromState.getValue(CeilingHangingSignBlock.ROTATION);
        boolean isAttached = fromState.getValue(CeilingHangingSignBlock.ATTACHED);
        return (rotation & 0b0000_1111) |
        (isAttached ? 0b0001_0000 : 0b0000_0000);
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int rotation = extraProtocolValue & 0b0000_1111;
        boolean isAttached = (extraProtocolValue & 0b0001_0000) == 0b0001_0000;
        return fromState
                .setValue(CeilingHangingSignBlock.ROTATION, rotation)
                .setValue(CeilingHangingSignBlock.ATTACHED, isAttached);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}