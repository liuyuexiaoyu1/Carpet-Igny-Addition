package com.liuyue.igny.mixins.rule.generateNetherPortal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BaseFireBlock.class)
public interface BaseFireBlockInvoker {
    @Invoker("isPortal")
    static boolean isPortal(Level level, BlockPos blockPos, Direction direction) {
        return false;
    }
}
