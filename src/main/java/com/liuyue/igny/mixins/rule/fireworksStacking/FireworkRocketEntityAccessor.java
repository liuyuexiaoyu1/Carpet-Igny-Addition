package com.liuyue.igny.mixins.rule.fireworksStacking;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FireworkRocketEntity.class)
public interface FireworkRocketEntityAccessor {
    @Accessor("life")
    int getLife();

    @Accessor("life")
    void setLife(int life);

    @Accessor("lifetime")
    int getLifetime();

    @Accessor("attachedToEntity")
    LivingEntity getAttachedToEntity();
}
