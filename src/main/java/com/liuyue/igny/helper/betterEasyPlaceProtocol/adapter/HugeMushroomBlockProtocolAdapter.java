package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HugeMushroomBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final HugeMushroomBlockProtocolAdapter INSTANCE = new HugeMushroomBlockProtocolAdapter();

    public HugeMushroomBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        return (fromState.getValue(HugeMushroomBlock.NORTH) ? 0b0000_0001 : 0b0000_0000) |
        (fromState.getValue(HugeMushroomBlock.EAST) ? 0b0000_0010 : 0b0000_0000) |
        (fromState.getValue(HugeMushroomBlock.SOUTH) ? 0b0000_0100 : 0b0000_0000) |
        (fromState.getValue(HugeMushroomBlock.WEST) ? 0b0000_1000 : 0b0000_0000) |
        (fromState.getValue(HugeMushroomBlock.UP) ? 0b0001_0000 : 0b0000_0000) |
        (fromState.getValue(HugeMushroomBlock.DOWN) ? 0b0010_0000 : 0b0000_0000);
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        return fromState
                .setValue(HugeMushroomBlock.NORTH, (extraProtocolValue & 0b0000_0001) == 0b0000_0001)
                .setValue(HugeMushroomBlock.EAST, (extraProtocolValue & 0b0000_0010) == 0b0000_0010)
                .setValue(HugeMushroomBlock.SOUTH, (extraProtocolValue & 0b0000_0100) == 0b0000_0100)
                .setValue(HugeMushroomBlock.WEST, (extraProtocolValue & 0b0000_1000) == 0b0000_1000)
                .setValue(HugeMushroomBlock.UP, (extraProtocolValue & 0b0001_0000) == 0b0001_0000)
                .setValue(HugeMushroomBlock.DOWN, (extraProtocolValue & 0b0010_0000) == 0b0010_0000);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}