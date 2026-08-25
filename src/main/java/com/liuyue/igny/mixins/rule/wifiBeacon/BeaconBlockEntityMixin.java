package com.liuyue.igny.mixins.rule.wifiBeacon;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin {
    @Unique
    private static final int MAX_RADIUS = 10 + 4 * 10;

    @Unique
    private static final Map<BlockPos, BlockPos> PARENT_MAP = new ConcurrentHashMap<>();

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;updateBase(Lnet/minecraft/world/level/Level;III)I")
    )
    private static int igny$wifiBeaconUpdateBase(Level level, int x, int y, int z, Operation<Integer> original, @Local(argsOnly = true) BeaconBlockEntity beacon) {
        int baseLevels = original.call(level, x, y, z);
        BlockPos pos = new BlockPos(x, y, z);

        if (baseLevels > 0) {
            PARENT_MAP.remove(pos);
            return baseLevels;
        }
        if (level == null) return baseLevels;
        if (!IGNYSettings.WIFI_BEACON.value()) return baseLevels;

        BeaconBlockEntity parent = findParent(level, pos);
        if (parent == null) {
            PARENT_MAP.remove(pos);
            return baseLevels;
        }

        BeaconBlockEntityAccessor self = (BeaconBlockEntityAccessor) beacon;
        BeaconBlockEntityAccessor parentAccessor = (BeaconBlockEntityAccessor) parent;
        int parentLevels = parentAccessor.igny$getLevels();
        if (parentLevels <= 0) {
            PARENT_MAP.remove(pos);
            return baseLevels;
        }

        self.igny$setLevels(parentLevels);
        self.igny$setPrimaryPower(parentAccessor.igny$getPrimaryPower());
        self.igny$setSecondaryPower(parentAccessor.igny$getSecondaryPower());
        PARENT_MAP.put(pos.immutable(), parent.getBlockPos().immutable());
        beacon.setChanged();
        return parentLevels;
    }


    @Unique
    private static BeaconBlockEntity findParent(Level level, BlockPos pos) {
        if (level == null) {
            return null;
        }
        if (!IGNYSettings.WIFI_BEACON.value()) return null;
        int searchRadius = MAX_RADIUS + getBeaconRangeExpand();
        int minX = pos.getX() - searchRadius;
        int maxX = pos.getX() + searchRadius;
        int minZ = pos.getZ() - searchRadius;
        int maxZ = pos.getZ() + searchRadius;

        BeaconBlockEntity best = null;
        int bestLevel = 0;
        int bestDistSq = Integer.MAX_VALUE;

        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                LevelChunk chunk = level.getChunk(chunkX, chunkZ);
                for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                    BlockEntity be = entry.getValue();
                    if (!(be instanceof BeaconBlockEntity beacon)) continue;
                    BlockPos beaconPos = beacon.getBlockPos();
                    if (beaconPos.equals(pos)) continue;
                    if (createsCycle(beaconPos, pos)) continue;
                    BeaconBlockEntityAccessor acc = (BeaconBlockEntityAccessor) beacon;
                    int beaconLevels = acc.igny$getLevels();
                    if (beaconLevels <= 0) continue;
                    if (acc.igny$getPrimaryPower() == null) continue;

                    int radius = 10 + beaconLevels * 10;
                    int rangeExpand = getBeaconRangeExpand();
                    if (rangeExpand > 0) radius += rangeExpand;

                    int dx = beaconPos.getX() - pos.getX();
                    int dz = beaconPos.getZ() - pos.getZ();
                    if (Math.abs(dx) > radius || Math.abs(dz) > radius) continue;
                    int distSq = dx * dx + dz * dz;
                    boolean worldHeight = isBeaconWorldHeight();
                    int minY = worldHeight ? level.getMinBuildHeight() : beaconPos.getY() - radius;
                    int maxY = worldHeight ? level.getMaxBuildHeight() : beaconPos.getY() + radius + level.getHeight();
                    if (pos.getY() < minY || pos.getY() > maxY) continue;

                    if (beaconLevels > bestLevel
                            || (beaconLevels == bestLevel && distSq < bestDistSq)) {
                        best = beacon;
                        bestLevel = beaconLevels;
                        bestDistSq = distSq;
                    }
                }
            }
        }
        return best;
    }

    @Unique
    private static int getBeaconRangeExpand() {
        Object value = com.liuyue.igny.utils.RuleUtil.getCarpetRulesValue("carpet-org-addition", "beaconRangeExpand");
        if (value instanceof Number number) {
            int v = number.intValue();
            return Math.max(0, v);
        }
        return 0;
    }

    @Unique
    private static boolean isBeaconWorldHeight() {
        Object value = com.liuyue.igny.utils.RuleUtil.getCarpetRulesValue("carpet-org-addition", "beaconWorldHeight");
        return Boolean.TRUE.equals(value);
    }

    @Unique
    private static boolean createsCycle(BlockPos candidatePos, BlockPos selfPos) {
        BlockPos cur = candidatePos.immutable();
        Set<BlockPos> visited = new HashSet<>();
        while (cur != null) {
            if (cur.equals(selfPos)) return true;
            if (!visited.add(cur)) return true;
            cur = PARENT_MAP.get(cur);
        }
        return false;
    }
}
