package com.liuyue.igny.mixins.rule.spawnMaxCountIgnoresChunkOverlap;

import carpet.CarpetServer;
import com.liuyue.igny.IGNYSettings;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.level.LocalMobCapCalculator$MobCounts", priority = 1100)
public class MobCountsMixin {
    @Shadow
    @Final
    private Object2IntMap<MobCategory> counts;

    @Inject(method = "canSpawn", at = @At(value = "HEAD"), cancellable = true)
    private void canSpawn(MobCategory category, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.spawnMaxCountIgnoresChunkOverlap && CarpetServer.minecraft_server != null) {
            cir.setReturnValue(this.counts.getOrDefault(category, 0) < category.getMaxInstancesPerChunk() * CarpetServer.minecraft_server.getPlayerCount());
        }
    }
}
