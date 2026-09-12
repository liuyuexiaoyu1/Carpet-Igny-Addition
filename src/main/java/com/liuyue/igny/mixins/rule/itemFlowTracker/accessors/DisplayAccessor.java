package com.liuyue.igny.mixins.rule.itemFlowTracker.accessors;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
public interface DisplayAccessor {
    @Invoker("setTransformation")
    void igny$setTransformation(Transformation transformation);

    @Invoker("setGlowColorOverride")
    void igny$setGlowColorOverride(int rgb);

    @Invoker("setBrightnessOverride")
    void igny$setBrightnessOverride(Brightness brightness);

    @Invoker("setViewRange")
    void igny$setViewRange(float range);
}
