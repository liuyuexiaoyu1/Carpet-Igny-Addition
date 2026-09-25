package com.liuyue.igny.mixins.util.virtualDisplay;

import com.liuyue.igny.utils.display.VirtualTracked;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ChunkMap.TrackedEntity.class)
public abstract class TrackedEntityMixin implements VirtualTracked {
    @Shadow
    @Final
    ServerEntity serverEntity;

    @Shadow
    SectionPos lastSectionPos;

    @Unique
    private Consumer<ServerPlayer> igny$pairingHook = player -> {};

    @Override
    public void igny$sendChanges() {
        this.serverEntity.sendChanges();
    }

    @Override
    public void igny$setPairingHook(Consumer<ServerPlayer> hook) {
        this.igny$pairingHook = hook;
    }

    @Override
    public SectionPos igny$lastSectionPos() {
        return this.lastSectionPos;
    }

    @Override
    public void igny$setLastSectionPos(SectionPos pos) {
        this.lastSectionPos = pos;
    }

    @WrapOperation(
            method = "updatePlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerEntity;addPairing(Lnet/minecraft/server/level/ServerPlayer;)V"
            )
    )
    private void igny$onPairing(ServerEntity instance, ServerPlayer player, Operation<Void> original) {
        original.call(instance, player);
        this.igny$pairingHook.accept(player);
    }
}
