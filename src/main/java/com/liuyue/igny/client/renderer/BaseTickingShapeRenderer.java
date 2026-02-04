package com.liuyue.igny.client.renderer;

import com.liuyue.igny.IGNYServer;
import ml.mypals.ryansrenderingkit.shapeManagers.ShapeManagers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseTickingShapeRenderer {
    protected final Map<ResourceLocation, ShapeData> TICKING_SHAPES = new ConcurrentHashMap<>();
    private static final List<BaseTickingShapeRenderer> ALL_RENDERERS = new ArrayList<>();
    protected final String prefix;

    private static final float ANIM_PCT_SPEED = 0.05f;
    private static final double MIN_BLOCK_STEP = 0.5;
    private static final float MIN_P_STEP = 0.02f;

    protected static class ShapeData {
        public long remaining;
        public float currentSize;
        public final BlockPos pos;
        public final int color;
        public final boolean depthTest;
        public final boolean smooth;
        public double minX, minY, minZ, maxX, maxY, maxZ;
        public double curMinX, curMinY, curMinZ, curMaxX, curMaxY, curMaxZ;
        public boolean isRemoving = false;

        public ShapeData(BlockPos pos, int color, long remaining, boolean depthTest, boolean smooth,
                         double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            this.pos = pos;
            this.color = color;
            this.remaining = remaining;
            this.depthTest = depthTest;
            this.smooth = smooth;
            this.currentSize = smooth ? 0f : 1f;
            this.minX = minX; this.minY = minY; this.minZ = minZ;
            this.maxX = maxX; this.maxY = maxY; this.maxZ = maxZ;

            Vec3 center = pos.getCenter();
            if (smooth) {
                this.curMinX = center.x; this.curMinY = center.y; this.curMinZ = center.z;
                this.curMaxX = center.x; this.curMaxY = center.y; this.curMaxZ = center.z;
            } else {
                this.curMinX = minX;
                this.curMinY = minY;
                this.curMinZ = minZ;
                this.curMaxX = maxX;
                this.curMaxY = maxY;
                this.curMaxZ = maxZ;
            }
        }
    }

    protected BaseTickingShapeRenderer(String prefix) {
        this.prefix = prefix;
        ALL_RENDERERS.add(this);
    }

    public static void tickAll() {
        for (BaseTickingShapeRenderer renderer : ALL_RENDERERS) renderer.tick();
    }

    private void tick() {
        if (TICKING_SHAPES.isEmpty()) return;

        TICKING_SHAPES.entrySet().removeIf(entry -> {
            ResourceLocation id = entry.getKey();
            ShapeData data = entry.getValue();

            if (data.remaining != Long.MAX_VALUE && !data.isRemoving) {
                data.remaining--;
                if (data.remaining < 0) {
                    if (data.smooth) data.isRemoving = true;
                    else { ShapeManagers.removeShape(id); return true; }
                }
            }

            boolean coordsMoving = moveCoords(data);

            float targetSize = data.isRemoving ? 0f : 1f;
            float sizeDiff = targetSize - data.currentSize;
            boolean sizeMoving = Math.abs(sizeDiff) > 0.0001f;

            if (sizeMoving) {
                float pStep = sizeDiff * ANIM_PCT_SPEED;
                if (Math.abs(pStep) < MIN_P_STEP) pStep = Math.signum(sizeDiff) * MIN_P_STEP;
                data.currentSize += pStep;
                data.currentSize = Math.max(0f, Math.min(1f, data.currentSize));
            }

            if (sizeMoving || coordsMoving) {
                updateShape(id, data);
            } else if (data.isRemoving && data.currentSize <= 0f) {
                ShapeManagers.removeShape(id);
                return true;
            }
            return false;
        });
    }

    private boolean moveCoords(ShapeData data) {
        double oldMX = data.curMinX;
        double oldMY = data.curMinY;
        double oldMZ = data.curMinZ;
        double oldMX2 = data.curMaxX;
        double oldMY2 = data.curMaxY;
        double oldMZ2 = data.curMaxZ;
        data.curMinX = moveValue(data.curMinX, data.minX);
        data.curMinY = moveValue(data.curMinY, data.minY);
        data.curMinZ = moveValue(data.curMinZ, data.minZ);
        data.curMaxX = moveValue(data.curMaxX, data.maxX);
        data.curMaxY = moveValue(data.curMaxY, data.maxY);
        data.curMaxZ = moveValue(data.curMaxZ, data.maxZ);
        return Math.abs(data.curMinX - oldMX) > 0.000001
                || Math.abs(data.curMinY - oldMY) > 0.000001
                || Math.abs(data.curMinZ - oldMZ) > 0.000001
                || Math.abs(data.curMaxX - oldMX2) > 0.000001
                || Math.abs(data.curMaxY - oldMY2) > 0.000001
                || Math.abs(data.curMaxZ - oldMZ2) > 0.000001;
    }

    private double moveValue(double current, double target) {
        double diff = target - current;
        if (Math.abs(diff) < 0.1) return target;

        double step = diff * (double)ANIM_PCT_SPEED;
        if (Math.abs(step) < MIN_BLOCK_STEP) step = Math.signum(diff) * MIN_BLOCK_STEP;
        if (Math.abs(step) >= Math.abs(diff)) return target;
        return current + step;
    }

    public void clear() {
        TICKING_SHAPES.forEach((id, data) -> {
            if (data.smooth) data.isRemoving = true;
            else { ShapeManagers.removeShape(id); data.remaining = -1; }
        });
    }

    protected abstract void updateShape(ResourceLocation id, ShapeData data);

    public void remove(BlockPos pos, String suffix) {
        ResourceLocation id = getId(pos);
        //#if MC <= 12006
        //$$ if (suffix != null) id = ResourceLocation.tryParse(id.toString() + "_" + suffix);
        //#else
        if (suffix != null) id = ResourceLocation.parse(id.toString() + "_" + suffix);
        //#endif
        ShapeData data = TICKING_SHAPES.get(id);
        if (data != null) {
            if (data.smooth) data.isRemoving = true;
            else data.remaining = -1;
        }
    }

    public ResourceLocation getId(BlockPos pos) {
        //#if MC <= 12006
        //$$ return new ResourceLocation(prefix + "_" + pos.getX() + "_" + pos.getY() + "_" + pos.getZ());
        //#else
        return ResourceLocation.fromNamespaceAndPath(IGNYServer.MOD_ID, prefix + "_" + pos.getX() + "_" + pos.getY() + "_" + pos.getZ());
        //#endif
    }

    protected Color parseColor(int argbColor) {
        return new Color((argbColor >> 16) & 0xFF, (argbColor >> 8) & 0xFF, argbColor & 0xFF, (argbColor >> 24) & 0xFF);
    }
}