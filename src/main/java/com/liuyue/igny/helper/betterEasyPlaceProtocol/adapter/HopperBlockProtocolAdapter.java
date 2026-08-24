package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HopperBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final HopperBlockProtocolAdapter INSTANCE = new HopperBlockProtocolAdapter();

    private static final int BIT_FACING_MASK = 0b0111;
    private static final int BIT_ENABLED_DISABLED = 0b1000;

    public HopperBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        Direction facing = fromState.getValue(HopperBlock.FACING);
        int v = protocolValue;
        v |= ((facing.get3DDataValue() + 1) & BIT_FACING_MASK);
        if (!fromState.getValue(HopperBlock.ENABLED)) {
            v |= BIT_ENABLED_DISABLED;
        }
        return v;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        BlockState state = fromState;
        int facing3d = (extraProtocolValue & BIT_FACING_MASK) - 1;
        if (facing3d >= 0 && facing3d <= 5) {
            Direction facing = Direction.from3DDataValue(facing3d);
            if (state.hasProperty(HopperBlock.FACING) && HopperBlock.FACING.getPossibleValues().contains(facing)) {
                state = state.setValue(HopperBlock.FACING, facing);
            }
        }
        boolean enabled = (extraProtocolValue & BIT_ENABLED_DISABLED) == 0;
        state = state.setValue(HopperBlock.ENABLED, enabled);
        return state;
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}
