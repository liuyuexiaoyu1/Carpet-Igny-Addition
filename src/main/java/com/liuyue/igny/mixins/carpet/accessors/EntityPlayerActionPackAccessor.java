package com.liuyue.igny.mixins.carpet.accessors;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import carpet.helpers.EntityPlayerActionPack;

@Mixin(EntityPlayerActionPack.class)
public interface EntityPlayerActionPackAccessor {
    @Accessor("player")
    ServerPlayer getPlayer();
}