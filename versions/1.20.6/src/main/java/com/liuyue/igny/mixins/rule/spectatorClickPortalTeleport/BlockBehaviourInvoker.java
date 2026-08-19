package com.liuyue.igny.mixins.rule.spectatorClickPortalTeleport;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockBehaviour.class)
public interface BlockBehaviourInvoker {
    @Invoker("entityInside")
    void invokeEntityInside(BlockState state, Level level, BlockPos pos, Entity entity);
}
