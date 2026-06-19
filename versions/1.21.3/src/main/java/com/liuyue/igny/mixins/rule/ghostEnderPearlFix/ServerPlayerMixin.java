package com.liuyue.igny.mixins.rule.ghostEnderPearlFix;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Shadow
    @Final
    private Set<ThrownEnderpearl> enderPearls;

    @Inject(method = "registerEnderPearl", at = @At(value = "RETURN"))
    private void loadAndSpawnEnderpearls(ThrownEnderpearl enderPearl, CallbackInfo ci) {
        if (IGNYSettings.GHOST_ENDER_PEARL_FIX.value()) {
            Set<UUID> seenUuids = new HashSet<>();
            this.enderPearls.removeIf(pearl -> !seenUuids.add(pearl.getUUID()));
        }
    }
}
