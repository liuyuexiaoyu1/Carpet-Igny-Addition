package com.liuyue.igny.mixins.rule.optimizedSpawning;

import carpet.utils.SpawnReporter;
import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {
    @WrapOperation(method = "spawnForChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;spawnCategoryForChunk(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V"))
    private static void spawnForChunk(MobCategory mobCategory, ServerLevel serverLevel, LevelChunk levelChunk, NaturalSpawner.SpawnPredicate spawnPredicate, NaturalSpawner.AfterSpawnCallback afterSpawnCallback, Operation<Void> original) {
        if (IGNYSettings.optimizedSpawning && mobCategory == MobCategory.MONSTER) {
            NaturalSpawner.SpawnState lastSpawner = serverLevel.getChunkSource().getLastSpawnState();
            if (lastSpawner != null) {
                ResourceKey<Level> dim = serverLevel.dimension();
                Object2IntMap<MobCategory> dimCounts = lastSpawner.getMobCategoryCounts();
                int chunkcount = SpawnReporter.chunkCounts.getOrDefault(dim, -1);
                int cur = dimCounts.getOrDefault(MobCategory.MONSTER, -1);
                int max = (int)(chunkcount * ((double)MobCategory.MONSTER.getMaxInstancesPerChunk() / SpawnReporter.MAGIC_NUMBER));
                if (cur >= max) {
                    return;
                }
            }
        }
        original.call(mobCategory, serverLevel, levelChunk, spawnPredicate, afterSpawnCallback);
    }
}
