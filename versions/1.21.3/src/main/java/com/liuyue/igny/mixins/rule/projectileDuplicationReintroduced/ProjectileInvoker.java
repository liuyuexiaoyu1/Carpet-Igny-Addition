package com.liuyue.igny.mixins.rule.projectileDuplicationReintroduced;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Projectile.class)
public interface ProjectileInvoker {
    @Invoker("hitTargetOrDeflectSelf")
    ProjectileDeflection invokeHitTargetOrDeflectSelf(HitResult hitResult);

    @Invoker("canHitEntity")
    boolean invokeCanHitEntity(Entity entity);

    @Invoker("updateRotation")
    void invokeUpdateRotation();
}
