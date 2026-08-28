package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BannerBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final BannerBlockProtocolAdapter INSTANCE = new BannerBlockProtocolAdapter();

    public BannerBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int rotation = fromState.getValue(BannerBlock.ROTATION);
        return (protocolValue & 0b1111_0000) | ((rotation + 1) & 0b0000_1111);
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int rotation = (extraProtocolValue & 0b0000_1111) - 1;
        if (rotation < 0) rotation = 0;
        return fromState.setValue(BannerBlock.ROTATION, rotation);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}
