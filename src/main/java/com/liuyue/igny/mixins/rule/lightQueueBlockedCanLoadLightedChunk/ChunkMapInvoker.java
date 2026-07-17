package com.liuyue.igny.mixins.rule.lightQueueBlockedCanLoadLightedChunk;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
//#if MC <= 12004
//$$ import net.minecraft.world.level.ChunkPos;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ChunkMapInvoker {
    //#if MC <= 12004
    //$$ @Invoker("releaseLightTicket")
    //$$ void invokeReleaseLightTicket(ChunkPos chunkPos);
    //#else
    @Invoker("getVisibleChunkIfPresent")
    ChunkHolder invokeGetVisibleChunkIfPresent(long key);
    //#endif
}