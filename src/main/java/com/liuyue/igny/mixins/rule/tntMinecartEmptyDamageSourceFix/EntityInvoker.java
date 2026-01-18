package com.liuyue.igny.mixins.rule.tntMinecartEmptyDamageSourceFix;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityInvoker {
    @Accessor("random")
    RandomSource getRandom();
}
