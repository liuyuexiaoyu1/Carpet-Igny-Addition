package com.liuyue.igny.mixins.rule.spawnMaxCountIgnoresChunkOverlap;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLevel.class)
public interface ServerLevelAccessor {
    @Accessor("chunkSource")
    ServerChunkCache getChunkSource();
}
