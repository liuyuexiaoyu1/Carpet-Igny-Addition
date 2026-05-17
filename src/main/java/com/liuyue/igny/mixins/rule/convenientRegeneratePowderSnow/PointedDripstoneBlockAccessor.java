package com.liuyue.igny.mixins.rule.convenientRegeneratePowderSnow;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(PointedDripstoneBlock.class)
public interface PointedDripstoneBlockAccessor {
    @Invoker("findRootBlock")
    static Optional<BlockPos> findRootBlock(Level level, BlockPos pos, BlockState state, int maxIterations) {
        return Optional.empty();
    }
}
