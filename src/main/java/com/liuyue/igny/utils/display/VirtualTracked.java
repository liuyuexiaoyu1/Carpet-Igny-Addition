package com.liuyue.igny.utils.display;

import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public interface VirtualTracked {
    void igny$sendChanges();

    void igny$setPairingHook(Consumer<ServerPlayer> hook);

    SectionPos igny$lastSectionPos();

    void igny$setLastSectionPos(SectionPos pos);
}
