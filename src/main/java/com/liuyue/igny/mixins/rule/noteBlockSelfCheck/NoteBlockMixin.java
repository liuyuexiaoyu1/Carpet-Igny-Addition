package com.liuyue.igny.mixins.rule.noteBlockSelfCheck;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin extends Block {
    @Shadow @Final public static BooleanProperty POWERED;

    @Shadow
    protected abstract void playNote(@Nullable Entity entity, BlockState blockState, Level level, BlockPos blockPos);

    public NoteBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    //#if MC <= 12005
    //$$ @SuppressWarnings("deprecation")
    //#endif
    //#if MC <= 12004
    //$$ public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
    //#else
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        //#endif
        if (IGNYSettings.NOTE_BLOCK_SELF_CHECK.value()) {
            boolean bl2 = level.hasNeighborSignal(blockPos);
            if (bl2 != blockState.getValue(POWERED)) {
                if (bl2) {
                    this.playNote(null, blockState, level, blockPos);
                }
                level.setBlock(blockPos, blockState.setValue(POWERED, bl2), 3);
            }
        }
    }
}
