package com.liuyue.igny.mixins.util.virtualDisplay;

import com.liuyue.igny.utils.display.VirtualSenders;
import com.liuyue.igny.utils.display.VirtualTracked;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
//? >= 26.3 ? import net.minecraft.world.entity.UpdateInterval;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin implements VirtualSenders {
    @Shadow
    @Final
    ServerLevel level;

    @Unique
    private final Map<Entity, ChunkMap.TrackedEntity> igny$virtualTracked = new IdentityHashMap<>();

    @Override
    public void igny$trackVirtual(Entity entity, Consumer<ServerPlayer> pairingHook) {
        if (this.igny$virtualTracked.containsKey(entity)) {
            return;
        }

        EntityType<?> type = entity.getType();
        int range = type.clientTrackingRange() * 16;

        if (range == 0) {
            return;
        }

        //#if >= 26.3
        /*$$UpdateInterval interval = type.hasUpdateInterval()
                ? UpdateInterval.periodic(type.updateInterval())
                : UpdateInterval.NEVER;
        ChunkMap.TrackedEntity tracked = ((ChunkMap) (Object) this).new TrackedEntity(
                entity, range, interval, type.trackDeltas());$$*/
        //#else
        ChunkMap.TrackedEntity tracked = ((ChunkMap) (Object) this).new TrackedEntity(
                entity, range, type.updateInterval(), type.trackDeltas());
        //#endif

        ((VirtualTracked) tracked).igny$setPairingHook(pairingHook);
        this.igny$virtualTracked.put(entity, tracked);
        tracked.updatePlayers(this.level.players());
    }

    @Override
    public void igny$untrackVirtual(Entity entity) {
        ChunkMap.TrackedEntity tracked = this.igny$virtualTracked.remove(entity);

        if (tracked != null) {
            tracked.broadcastRemoved();
        }
    }

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "RETURN"))
    private void igny$tickVirtualDisplays(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        if (this.igny$virtualTracked.isEmpty()) {
            return;
        }

        List<ServerPlayer> players = this.level.players();

        for (ChunkMap.TrackedEntity tracked : this.igny$virtualTracked.values()) {
            tracked.updatePlayers(players);
            ((VirtualTracked) tracked).igny$sendChanges();
        }
    }
}
