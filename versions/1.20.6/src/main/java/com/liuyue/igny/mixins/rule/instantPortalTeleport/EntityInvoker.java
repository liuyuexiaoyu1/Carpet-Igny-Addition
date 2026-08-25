package com.liuyue.igny.mixins.rule.instantPortalTeleport;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityInvoker {
    @Invoker("handleNetherPortal")
    void igny$invokeHandleNetherPortal();
}