package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import net.minecraft.world.entity.animal.HappyGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HappyGhast.class)
public interface HappyGhastInvoker {
    @Invoker("scanPlayerAboveGhast")
    boolean invokeScanPlayerAboveGhast();
}
