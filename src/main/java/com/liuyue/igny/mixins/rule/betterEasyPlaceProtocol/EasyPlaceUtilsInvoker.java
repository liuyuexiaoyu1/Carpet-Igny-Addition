package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import fi.dy.masa.litematica.util.EasyPlaceUtils;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EasyPlaceUtils.class)
public interface EasyPlaceUtilsInvoker {
    @Invoker("cacheEasyPlacePosition")
    static void invokeCacheEasyPlacePosition(BlockPos pos) {}
}
