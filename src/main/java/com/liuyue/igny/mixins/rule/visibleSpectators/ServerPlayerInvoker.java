package com.liuyue.igny.mixins.rule.visibleSpectators;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayer.class)
public interface ServerPlayerInvoker {
    @Invoker("updateInvisibilityStatus")
    void updateInvisibilityStatus();
}
