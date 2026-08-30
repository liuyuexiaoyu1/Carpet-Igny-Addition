package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final CommandBlockProtocolAdapter INSTANCE = new CommandBlockProtocolAdapter();

    private static final int BIT_FACING_MASK = 0b111;
    private static final int BIT_CONDITIONAL = 0b0001_0000;

    public CommandBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        // Command block FACING is the full 6-direction property; encode it as (3D data value + 1)
        // so that DOWN (value 0) still produces a non-zero addition and is never skipped.
        int facingBits = ((fromState.getValue(CommandBlock.FACING).get3DDataValue() + 1) & BIT_FACING_MASK);
        boolean isConditional = fromState.getValue(CommandBlock.CONDITIONAL);
        int conditionalBits = (isConditional ? BIT_CONDITIONAL : 0);
        return protocolValue | facingBits | conditionalBits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isConditional = (extraProtocolValue & BIT_CONDITIONAL) == BIT_CONDITIONAL;
        BlockState state = fromState.setValue(CommandBlock.CONDITIONAL, isConditional);

        int facing3d = (extraProtocolValue & BIT_FACING_MASK) - 1;
        if (facing3d >= 0 && facing3d <= 5) {
            Direction facing = Direction.from3DDataValue(facing3d);
            if (CommandBlock.FACING.getPossibleValues().contains(facing)) {
                state = state.setValue(CommandBlock.FACING, facing);
            }
        }
        return state;
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}
