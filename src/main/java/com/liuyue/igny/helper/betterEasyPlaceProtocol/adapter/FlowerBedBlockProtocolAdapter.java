package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.MultiStageBlockProtocolStateAdapter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlowerBedBlockProtocolAdapter implements MultiStageBlockProtocolStateAdapter, BlockProtocolStateAdapter {
    public static final FlowerBedBlockProtocolAdapter INSTANCE = new FlowerBedBlockProtocolAdapter();

    //#if MC >= 12105
    //$$ private static final Property<Direction> FACING = net.minecraft.world.level.block.FlowerBedBlock.FACING;
    //$$ private static final IntegerProperty AMOUNT = net.minecraft.world.level.block.FlowerBedBlock.AMOUNT;
    //#else
    private static final Property<Direction> FACING = net.minecraft.world.level.block.PinkPetalsBlock.FACING;
    private static final IntegerProperty AMOUNT = net.minecraft.world.level.block.PinkPetalsBlock.AMOUNT;
    //#endif

    public FlowerBedBlockProtocolAdapter() {
    }

    //#if MC >= 12105
    //$$ private static boolean isTarget(BlockState state) {
    //$$     return state.getBlock() instanceof net.minecraft.world.level.block.FlowerBedBlock;
    //$$ }
    //#else
    private static boolean isTarget(BlockState state) {
        return state.getBlock() instanceof net.minecraft.world.level.block.PinkPetalsBlock;
    }
    //#endif

    @Override
    public void igny$setLoopCount(LoopContext ctx) {
        int curAmount = isTarget(ctx.stateClient) ? ctx.stateClient.getValue(AMOUNT) : 0;
        int targetAmount = ctx.stateSchematic.getValue(AMOUNT);

        if (targetAmount > curAmount) {
            ctx.loopCount = targetAmount - curAmount;
        } else {
            ctx.loopCount = 0;
        }
    }

    @Override
    public int igny$toProtocolValueLoop(LoopContext ctx) {
        int facingOrdinal = ctx.stateSchematic.getValue(FACING).ordinal() - 2;
        int maxAmount = ctx.stateSchematic.getValue(AMOUNT) - 1;

        return ((facingOrdinal & 0b0011) << 2) |
        (maxAmount & 0b0011);
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int facingOrdinal = ((extraProtocolValue & 0b1100) >>> 2) + 2;
        int maxAmount = (extraProtocolValue & 0b0011) + 1;

        Level world = context.getLevel();
        BlockState blockWorldState = world.getBlockState(context.getClickedPos());

        BlockState newState = fromState;

        
        if (!isTarget(blockWorldState)) {
            newState = newState.setValue(FACING, Direction.values()[facingOrdinal]);
        }

        if (newState.getValue(AMOUNT) > maxAmount) {
            return null;
        }

        return newState;
    }

    
    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        return 0;
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }
}