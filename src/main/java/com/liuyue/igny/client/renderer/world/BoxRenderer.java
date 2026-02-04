package com.liuyue.igny.client.renderer.world;

import com.liuyue.igny.client.renderer.BaseTickingShapeRenderer;
import ml.mypals.ryansrenderingkit.builders.shapeBuilders.ShapeGenerator;
import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shapeManagers.ShapeManagers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.awt.Color;

public class BoxRenderer extends BaseTickingShapeRenderer {
    public static final BoxRenderer INSTANCE = new BoxRenderer();
    private BoxRenderer() { super("box"); }

    public static void addBox(BlockPos pos, int color, int durationTicks, boolean permanent, boolean depthTest,
                              double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
                              boolean withLine, boolean lineDepthTest, boolean smooth) {

        ResourceLocation id = INSTANCE.getId(pos);
        INSTANCE.processUpdate(id, pos, color, durationTicks, permanent, depthTest, smooth, minX, minY, minZ, maxX, maxY, maxZ);

        if (withLine) {
            //#if MC <= 12006
            //$$ ResourceLocation lineId = ResourceLocation.tryParse(id.toString() + "_line");
            //#else
            ResourceLocation lineId = ResourceLocation.parse(id.toString() + "_line");
            //#endif
            INSTANCE.processUpdate(lineId, pos, 0xFFFFFFFF, durationTicks, permanent, lineDepthTest, smooth, minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    private void processUpdate(ResourceLocation id, BlockPos pos, int color, int ticks, boolean permanent,
                               boolean depthTest, boolean smooth, double minX, double minY, double minZ,
                               double maxX, double maxY, double maxZ) {
        long remaining = permanent ? Long.MAX_VALUE : (long) ticks;

        if (TICKING_SHAPES.containsKey(id)) {
            ShapeData data = TICKING_SHAPES.get(id);
            data.remaining = remaining;
            data.isRemoving = false;
            data.minX = minX;
            data.minY = minY;
            data.minZ = minZ;
            data.maxX = maxX;
            data.maxY = maxY;
            data.maxZ = maxZ;
        } else {
            ShapeData data = new ShapeData(pos, color, remaining, depthTest, smooth, minX, minY, minZ, maxX, maxY, maxZ);
            TICKING_SHAPES.put(id, data);
            updateShape(id, data);
        }
    }

    @Override
    protected void updateShape(ResourceLocation id, ShapeData data) {
        Color color = parseColor(data.color);
        Vec3 center = data.pos.getCenter();
        double p = data.currentSize;

        if (!data.smooth){
            renderBox(id, color, data.depthTest, data.minX, data.minY, data.minZ, data.maxX, data.maxY, data.maxZ);
            data.curMinX = data.minX;
            data.curMinY = data.minY;
            data.curMinZ = data.minZ;
            data.curMaxX = data.maxX;
            data.curMaxY = data.maxY;
            data.curMaxZ = data.maxZ;
            return;
        }

        boolean allCoordsReached =
                Math.abs(data.curMinX - data.minX) < 0.001 &&
                        Math.abs(data.curMinY - data.minY) < 0.001 &&
                        Math.abs(data.curMinZ - data.minZ) < 0.001 &&
                        Math.abs(data.curMaxX - data.maxX) < 0.001 &&
                        Math.abs(data.curMaxY - data.maxY) < 0.001 &&
                        Math.abs(data.curMaxZ - data.maxZ) < 0.001;

        if (!data.isRemoving && allCoordsReached) {
            renderBox(id, color, data.depthTest, data.minX, data.minY, data.minZ, data.maxX, data.maxY, data.maxZ);
        } else {
            double rMinX = Mth.lerp(p, center.x, data.curMinX);
            double rMinY = Mth.lerp(p, center.y, data.curMinY);
            double rMinZ = Mth.lerp(p, center.z, data.curMinZ);
            double rMaxX = Mth.lerp(p, center.x, data.curMaxX);
            double rMaxY = Mth.lerp(p, center.y, data.curMaxY);
            double rMaxZ = Mth.lerp(p, center.z, data.curMaxZ);
            renderBox(id, color, data.depthTest, rMinX, rMinY, rMinZ, rMaxX, rMaxY, rMaxZ);
        }
    }

    private void renderBox(ResourceLocation id, Color color, boolean dt, double x1, double y1, double z1, double x2, double y2, double z2) {
        Vec3 min = new Vec3(x1, y1, z1);
        Vec3 max = new Vec3(x2, y2, z2);
        Shape shape = id.getPath().endsWith("_line")
                ? ShapeGenerator.generateBoxWireframe().seeThrough(!dt).aabb(min, max).color(color).build(Shape.RenderingType.BATCH)
                : ShapeGenerator.generateBoxFace().seeThrough(!dt).aabb(min, max).color(color).build(Shape.RenderingType.BATCH);
        ShapeManagers.addShape(id, shape);
    }
}