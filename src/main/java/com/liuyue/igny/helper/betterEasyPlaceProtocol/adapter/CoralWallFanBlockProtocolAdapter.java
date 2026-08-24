package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoralWallFanBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final CoralWallFanBlockProtocolAdapter INSTANCE = new CoralWallFanBlockProtocolAdapter();

    public CoralWallFanBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        return (fromState.getValue(BaseCoralWallFanBlock.FACING).ordinal() - 2) & 0b0000_0011;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {

        int facingIndex = (extraProtocolValue & 0b0000_0011) + 2;
        Direction facing = Direction.values()[facingIndex];
        return fromState.setValue(BaseCoralWallFanBlock.FACING, facing);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}
