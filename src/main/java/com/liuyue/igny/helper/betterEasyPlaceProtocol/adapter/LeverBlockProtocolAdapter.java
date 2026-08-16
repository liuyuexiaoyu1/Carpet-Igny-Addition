package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeverBlockProtocolAdapter implements BlockProtocolStateAdapter {
    public static final LeverBlockProtocolAdapter INSTANCE = new LeverBlockProtocolAdapter();

    public LeverBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int faceOrdinal = fromState.getValue(FaceAttachedHorizontalDirectionalBlock.FACE).ordinal();
        boolean isPowered = fromState.getValue(LeverBlock.POWERED);
        int bits =
                ((faceOrdinal & 0b0000_0011) << 4) |
                (isPowered ? 0b0100_0000 : 0b0000_0000);
        return protocolValue | bits;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        int faceOrdinal = ((extraProtocolValue & 0b0011_0000) >>> 4) % 3;
        boolean isPowered = (extraProtocolValue & 0b0100_0000) == 0b0100_0000;
        return fromState
                .setValue(FaceAttachedHorizontalDirectionalBlock.FACE, AttachFace.values()[faceOrdinal])
                .setValue(LeverBlock.POWERED, isPowered);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }
}