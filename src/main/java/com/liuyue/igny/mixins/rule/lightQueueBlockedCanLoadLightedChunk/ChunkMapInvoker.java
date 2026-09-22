package com.liuyue.igny.mixins.rule.lightQueueBlockedCanLoadLightedChunk;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
//?<= 1.20.4 ? import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ChunkMapInvoker {
    //#if <= 1.20.4
    /*$$@Invoker("releaseLightTicket")
    void invokeReleaseLightTicket(ChunkPos chunkPos);$$*/
    //#endif
    @Invoker("getVisibleChunkIfPresent")
    ChunkHolder invokeGetVisibleChunkIfPresent(long key);
}