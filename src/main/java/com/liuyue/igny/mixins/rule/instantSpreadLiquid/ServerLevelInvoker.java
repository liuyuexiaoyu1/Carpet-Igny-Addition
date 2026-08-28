package com.liuyue.igny.mixins.rule.instantSpreadLiquid;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerLevel.class)
public interface ServerLevelInvoker {
    @Invoker("tickFluid")
    void invokeTickFluid(final BlockPos pos, final Fluid type);
}
