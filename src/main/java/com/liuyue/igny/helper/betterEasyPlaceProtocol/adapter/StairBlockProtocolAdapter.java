package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StairBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final StairBlockProtocolAdapter INSTANCE = new StairBlockProtocolAdapter();

    public StairBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int facingOrdinal = fromState.getValue(StairBlock.FACING).ordinal() - 2;
        int halfOrdinal = fromState.getValue(StairBlock.HALF).ordinal();
        int shapeOrdinal = fromState.getValue(StairBlock.SHAPE).ordinal();
        return (facingOrdinal & 0b0000_0011) |
        (halfOrdinal & 0b0000_0001) << 2 |
        (shapeOrdinal & 0b0000_0111) << 3;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int facingOrdinal = (extraProtocolValue & 0b0000_0011) + 2;
        int halfOrdinal = (extraProtocolValue & 0b0000_0100) >>> 2;
        int shapeOrdinal = (extraProtocolValue & 0b0011_1000) >>> 3;
        return fromState
                .setValue(StairBlock.FACING, Direction.values()[facingOrdinal])
                .setValue(StairBlock.HALF, Half.values()[halfOrdinal])
                .setValue(StairBlock.SHAPE, StairsShape.values()[shapeOrdinal]);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}