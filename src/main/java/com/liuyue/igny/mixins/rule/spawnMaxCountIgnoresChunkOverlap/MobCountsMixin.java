package com.liuyue.igny.mixins.rule.spawnMaxCountIgnoresChunkOverlap;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.level.LocalMobCapCalculator$MobCounts", priority = 1100)
public class MobCountsMixin {
    @WrapOperation(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/MobCategory;getMaxInstancesPerChunk()I"))
    private int canSpawn(MobCategory instance, Operation<Integer> original) {
        if (IGNYSettings.spawnMaxCountIgnoresChunkOverlap && IGNYSettings.cachedServer != null) {
            return (original.call(instance) * IGNYSettings.cachedServer.getPlayerCount());
        }
        return original.call(instance);
    }
}
