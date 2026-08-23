package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final CommandBlockProtocolAdapter INSTANCE = new CommandBlockProtocolAdapter();

    public CommandBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        boolean isConditional = fromState.getValue(CommandBlock.CONDITIONAL);

        int bits = (isConditional ? 0b0001_0000 : 0b0000_0000);
        return protocolValue | bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        boolean isConditional = (extraProtocolValue & 0b0001_0000) == 0b0001_0000;
        return fromState.setValue(CommandBlock.CONDITIONAL, isConditional);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}