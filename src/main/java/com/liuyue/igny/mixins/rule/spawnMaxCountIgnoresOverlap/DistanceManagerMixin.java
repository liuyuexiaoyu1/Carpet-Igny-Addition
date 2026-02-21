package com.liuyue.igny.mixins.rule.spawnMaxCountIgnoresOverlap;

import com.liuyue.igny.IGNYSettings;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DistanceManager.class)
public class DistanceManagerMixin {
    @Shadow
    @Final
    Long2ObjectMap<ObjectSet<ServerPlayer>> playersPerChunk;

    @Inject(method = "getNaturalSpawnChunkCount", at = @At(value = "HEAD"), cancellable = true)
    private void getNaturalSpawnChunkCount(CallbackInfoReturnable<Integer> cir) {
        if (this.playersPerChunk.isEmpty()) return;
        MinecraftServer server = this.playersPerChunk.values().iterator().next().iterator().next().level().getServer();
        if (IGNYSettings.spawnMaxCountIgnoresChunkOverlap && server != null) {
            cir.setReturnValue(server.getPlayerCount() * 289);
        }
    }
}
