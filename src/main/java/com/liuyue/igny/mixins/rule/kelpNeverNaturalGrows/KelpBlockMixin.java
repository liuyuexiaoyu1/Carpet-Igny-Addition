package com.liuyue.igny.mixins.rule.kelpNeverNaturalGrows;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(KelpBlock.class)
public abstract class KelpBlockMixin extends GrowingPlantHeadBlock {
    protected KelpBlockMixin(Properties properties, Direction growthDirection, VoxelShape shape, boolean scheduleFluidTicks, double growPerTickProbability) {
        super(properties, growthDirection, shape, scheduleFluidTicks, growPerTickProbability);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (IGNYSettings.KELP_NEVER_NATURAL_GROWS.value()) {
            return;
        }
        super.randomTick(state, level, pos, random);
    }
}
