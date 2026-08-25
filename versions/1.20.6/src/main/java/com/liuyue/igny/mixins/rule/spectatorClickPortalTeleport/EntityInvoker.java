package com.liuyue.igny.mixins.rule.spectatorClickPortalTeleport;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityInvoker {
    @Invoker("handleNetherPortal")
    void invokeHandleNetherPortal();
}
