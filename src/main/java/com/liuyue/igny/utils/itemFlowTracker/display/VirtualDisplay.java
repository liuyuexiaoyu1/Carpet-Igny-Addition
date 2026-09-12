package com.liuyue.igny.utils.itemFlowTracker.display;

import com.liuyue.igny.mixins.rule.itemFlowTracker.accessors.BlockDisplayAccessor;
import com.liuyue.igny.mixins.rule.itemFlowTracker.accessors.DisplayAccessor;
import com.liuyue.igny.mixins.rule.itemFlowTracker.accessors.ItemDisplayAccessor;
import com.liuyue.igny.mixins.rule.itemFlowTracker.accessors.SetPassengersPacketAccessor;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.mojang.math.Transformation;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class VirtualDisplay {
    //#if MC >= 26.2
    //$$ private static final EntityType<Display.BlockDisplay> BLOCK_TYPE =
    //$$         net.minecraft.world.entity.EntityTypes.BLOCK_DISPLAY;
    //$$ private static final EntityType<Display.ItemDisplay> ITEM_TYPE =
    //$$         net.minecraft.world.entity.EntityTypes.ITEM_DISPLAY;
    //#else
    private static final EntityType<Display.BlockDisplay> BLOCK_TYPE = EntityType.BLOCK_DISPLAY;
    private static final EntityType<Display.ItemDisplay> ITEM_TYPE = EntityType.ITEM_DISPLAY;
    //#endif

    private static final double VIEW_RANGE = 96.0D;
    private static final double VIEW_RANGE_SQR = VIEW_RANGE * VIEW_RANGE;
    private static final float VIEW_RANGE_DATA = 2.0F;

    private final ServerLevel level;
    private final Display entity;
    private final Set<UUID> viewers = new HashSet<>();

    @Nullable
    private Entity vehicle;

    private boolean dataDirty = true;

    private VirtualDisplay(ServerLevel level, Display entity) {
        this.level = level;
        this.entity = entity;
        this.entity.setNoGravity(true);
        //#if MC >= 26.3
        //$$ this.entity.setPermanentlyInvulnerable(true);
        //#else
        this.entity.setInvulnerable(true);
        //#endif
        ((DisplayAccessor) this.entity).igny$setViewRange(VIEW_RANGE_DATA);
    }

    public static VirtualDisplay block(ServerLevel level, double x, double y, double z, BlockState state) {
        Display.BlockDisplay display = new Display.BlockDisplay(BLOCK_TYPE, level);
        display.setPos(x, y, z);
        ((BlockDisplayAccessor) display).igny$setBlockState(state);
        return new VirtualDisplay(level, display);
    }

    public static VirtualDisplay item(ServerLevel level, double x, double y, double z, ItemStack stack) {
        Display.ItemDisplay display = new Display.ItemDisplay(ITEM_TYPE, level);
        display.setPos(x, y, z);
        ItemDisplayAccessor accessor = (ItemDisplayAccessor) display;
        accessor.igny$setItemStack(stack);
        accessor.igny$setItemTransform(ItemDisplayContext.NONE);
        return new VirtualDisplay(level, display);
    }

    public VirtualDisplay glow(@Nullable TrackMark mark) {
        if (mark != null) {
            this.entity.setGlowingTag(true);
            ((DisplayAccessor) this.entity).igny$setGlowColorOverride(mark.rgb());
            this.dataDirty = true;
        }

        return this;
    }

    public VirtualDisplay bright() {
        ((DisplayAccessor) this.entity).igny$setBrightnessOverride(Brightness.FULL_BRIGHT);
        this.dataDirty = true;
        return this;
    }

    public VirtualDisplay transform(@Nullable Transformation transformation) {
        if (transformation != null) {
            ((DisplayAccessor) this.entity).igny$setTransformation(transformation);
            this.entity.setPos(this.entity.getX(), this.entity.getY(), this.entity.getZ());
            this.dataDirty = true;
        }

        return this;
    }

    public VirtualDisplay ride(Entity vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public void sync() {
        @Nullable List<SynchedEntityData.DataValue<?>> data =
                this.dataDirty ? this.entity.getEntityData().getNonDefaultValues() : null;

        if (data != null && data.isEmpty()) {
            data = null;
        }

        double x = this.entity.getX();
        double y = this.entity.getY();
        double z = this.entity.getZ();
        int id = this.entity.getId();

        for (ServerPlayer player : this.level.players()) {
            UUID viewer = player.getUUID();

            if (!this.visibleTo(player, x, y, z)) {
                if (this.viewers.remove(viewer)) {
                    player.connection.send(new ClientboundRemoveEntitiesPacket(id));
                }
                continue;
            }

            if (!this.viewers.add(viewer)) {
                if (data != null) {
                    player.connection.send(new ClientboundSetEntityDataPacket(id, data));
                }
                continue;
            }

            player.connection.send(new ClientboundAddEntityPacket(
                    id, this.entity.getUUID(), x, y, z,
                    0.0F, 0.0F, this.entity.getType(), 0, Vec3.ZERO, 0.0D));

            if (data != null) {
                player.connection.send(new ClientboundSetEntityDataPacket(id, data));
            }

            if (this.vehicle != null) {
                player.connection.send(this.passengersPacket());
            }
        }

        this.dataDirty = false;
    }

    public void remove() {
        if (this.viewers.isEmpty()) {
            return;
        }

        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(this.entity.getId());

        for (ServerPlayer player : this.level.players()) {
            if (this.viewers.remove(player.getUUID())) {
                player.connection.send(packet);
            }
        }

        this.viewers.clear();
    }

    private boolean visibleTo(ServerPlayer player, double x, double y, double z) {
        if (player.distanceToSqr(x, y, z) > VIEW_RANGE_SQR) {
            return false;
        }

        if (this.vehicle == null) {
            return true;
        }

        if (player.level() != this.level) {
            return false;
        }

        return this.level.getChunkSource().chunkMap
                .getPlayers(this.vehicle.chunkPosition(), false)
                .contains(player);
    }

    private ClientboundSetPassengersPacket passengersPacket() {
        if (this.vehicle == null) return null;
        ClientboundSetPassengersPacket packet = new ClientboundSetPassengersPacket(this.vehicle);
        ((SetPassengersPacketAccessor) packet).igny$setPassengers(new int[]{this.entity.getId()});
        return packet;
    }
}
