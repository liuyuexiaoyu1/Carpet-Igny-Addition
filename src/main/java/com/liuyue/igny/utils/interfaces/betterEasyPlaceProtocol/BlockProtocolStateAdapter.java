package com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockProtocolStateAdapter {
    int igny$toProtocolValue(int protocolValue, BlockState fromState);

    @Nullable
    BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context);

    @NotNull
    ProtocolType igny$getProtocolType();

    enum ProtocolType {
        ADDED,
        REPLACE,
    }
}