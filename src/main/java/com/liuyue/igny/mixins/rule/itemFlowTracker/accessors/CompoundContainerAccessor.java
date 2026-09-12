package com.liuyue.igny.mixins.rule.itemFlowTracker.accessors;

import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CompoundContainer.class)
public interface CompoundContainerAccessor {
    @Accessor("container1")
    Container igny$container1();

    @Accessor("container2")
    Container igny$container2();
}
