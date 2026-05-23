package com.liuyue.igny.mixins.rule.ghostEnderPearlFix;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Shadow
    @Final
    private Set<ThrownEnderpearl> enderPearls;

    //#if MC >= 26.2
    //$$ @Inject(method = "lambda$loadAndSpawnEnderpearls$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;loadEntityRecursive(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/EntitySpawnReason;Ljava/util/function/Function;)Lnet/minecraft/world/entity/Entity;", shift = At.Shift.AFTER))
    //#else
    @Inject(method = "method_64127", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;loadEntityRecursive(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/EntitySpawnReason;Ljava/util/function/Function;)Lnet/minecraft/world/entity/Entity;", shift = At.Shift.AFTER))
    //#endif
    private void loadAndSpawnEnderpearls(Tag tag, CallbackInfo ci) {
        if (IGNYSettings.GHOST_ENDER_PEARL_FIX.value()) {
            Set<UUID> seenUuids = new HashSet<>();
            this.enderPearls.removeIf(pearl -> !seenUuids.add(pearl.getUUID()));
        }
    }
}
