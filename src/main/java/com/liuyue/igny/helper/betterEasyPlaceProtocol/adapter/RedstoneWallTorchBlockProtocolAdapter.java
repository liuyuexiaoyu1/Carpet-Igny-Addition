package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneWallTorchBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final RedstoneWallTorchBlockProtocolAdapter INSTANCE = new RedstoneWallTorchBlockProtocolAdapter();

    public RedstoneWallTorchBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        boolean isLit = fromState.getValue(RedstoneWallTorchBlock.LIT);
        return isLit ? 0b0001 : 0b0000;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isLit = (extraProtocolValue & 0b0001) == 0b0001;
        return fromState.setValue(RedstoneWallTorchBlock.LIT, isLit);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}