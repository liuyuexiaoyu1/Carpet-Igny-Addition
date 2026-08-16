package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoralWallFanBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final CoralWallFanBlockProtocolAdapter INSTANCE = new CoralWallFanBlockProtocolAdapter();

    public CoralWallFanBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int bits = (fromState.getValue(BaseCoralWallFanBlock.FACING).ordinal() - 2) & 0b0000_0011;
        if (fromState.getValue(BlockStateProperties.WATERLOGGED)) {
            bits |= 0b0000_0100;
        }
        return bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        
        int facingIndex = (extraProtocolValue & 0b0000_0011) + 2;
        Direction facing = Direction.values()[facingIndex];

        
        BlockPos attachPos = context.getClickedPos().relative(facing.getOpposite());
        if (!context.getLevel().getBlockState(attachPos).isFaceSturdy(context.getLevel(), attachPos, facing)) {
            return null;
        }

        return fromState
                .setValue(BaseCoralWallFanBlock.FACING, facing)
                .setValue(BlockStateProperties.WATERLOGGED, (extraProtocolValue & 0b0000_0100) != 0);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}