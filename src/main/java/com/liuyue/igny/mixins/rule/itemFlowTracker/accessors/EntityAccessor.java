package com.liuyue.igny.mixins.rule.itemFlowTracker.accessors;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("vehicle")
    void igny$setVehicle(@Nullable Entity vehicle);
}
