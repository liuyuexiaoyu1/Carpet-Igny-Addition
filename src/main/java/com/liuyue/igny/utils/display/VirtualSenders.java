package com.liuyue.igny.utils.display;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public interface VirtualSenders {
    void igny$trackVirtual(Entity entity, Consumer<ServerPlayer> pairingHook);

    void igny$untrackVirtual(Entity entity);
}
