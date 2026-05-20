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

public class HighlightBlocksRenderer extends BaseTickingShapeRenderer {
    public static final HighlightBlocksRenderer INSTANCE = new HighlightBlocksRenderer();

    private HighlightBlocksRenderer() {
        super("highlight_blocks");
    }

    public static void addHighlight(BlockPos pos, int argbColor, int durationTicks, boolean permanent) {
        ResourceLocation id = INSTANCE.getId(pos);
        long ticks = permanent ? Long.MAX_VALUE : (long) durationTicks;
        double minX = pos.getX();
        double minY = pos.getY();
        double minZ = pos.getZ();
        double maxX = pos.getX() + 1.0;
        double maxY = pos.getY() + 1.0;
        double maxZ = pos.getZ() + 1.0;
        ShapeData data = new ShapeData(pos, argbColor, ticks, true, false,
                minX, minY, minZ, maxX, maxY, maxZ);

        INSTANCE.TICKING_SHAPES.put(id, data);
        INSTANCE.updateShape(id, data);
    }

    @Override
    protected void updateShape(ResourceLocation id, ShapeData data) {
        Color color = parseColor(data.color);
        //#if MC >= 26.2
        //$$ Vec3 center = Vec3.atCenterOf(data.pos);
        //#else
        Vec3 center = data.pos.getCenter();
        //#endif
        double p = data.currentSize;
        Vec3 min = new Vec3(
                Mth.lerp(p, center.x, data.minX),
                Mth.lerp(p, center.y, data.minY),
                Mth.lerp(p, center.z, data.minZ)
        );
        Vec3 max = new Vec3(
                Mth.lerp(p, center.x, data.maxX),
                Mth.lerp(p, center.y, data.maxY),
                Mth.lerp(p, center.z, data.maxZ)
        );
        var box = ShapeGenerator.generateBoxFace()
                .seeThrough(true)
                .aabb(min, max)
                .color(color)
                .build(Shape.RenderingType.BATCH);
        ShapeManagers.addShape(id, box);
    }
}