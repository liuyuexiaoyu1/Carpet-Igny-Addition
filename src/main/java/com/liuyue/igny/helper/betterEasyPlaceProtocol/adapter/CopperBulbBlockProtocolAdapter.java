package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

//#if MC >= 12003

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CopperBulbBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final CopperBulbBlockProtocolAdapter INSTANCE = new CopperBulbBlockProtocolAdapter();

    public CopperBulbBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int lit = fromState.getValue(CopperBulbBlock.LIT) ? 0b0001 : 0b0000;
        int powered = fromState.getValue(CopperBulbBlock.POWERED) ? 0b0010 : 0b0000;
        return lit | powered;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        
        boolean lit = (extraProtocolValue & 0b0001) == 0b0001;
        boolean powered = (extraProtocolValue & 0b0010) == 0b0010;
        return fromState.setValue(CopperBulbBlock.LIT, lit).setValue(CopperBulbBlock.POWERED, powered);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}
//#endif