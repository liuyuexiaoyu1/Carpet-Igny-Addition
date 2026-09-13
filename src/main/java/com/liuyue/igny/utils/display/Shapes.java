package com.liuyue.igny.utils.display;

import com.mojang.math.Transformation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class Shapes {
    private static final float OUTLINE_SCALE = 1.02F;
    private static final float PATH_THICKNESS = 0.06F;
    private static final double ENTITY_PADDING = 0.02D;

    public static Transformation outline() {
        float offset = (OUTLINE_SCALE - 1.0F) / 2.0F;
        return of(
                new Vector3f(-offset, -offset, -offset),
                new Quaternionf(),
                new Vector3f(OUTLINE_SCALE, OUTLINE_SCALE, OUTLINE_SCALE));
    }

    public static Transformation outline(BlockState state, float width) {
        Quaternionf rotation = new Quaternionf();

        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            rotation.rotateY((float) Math.toRadians(-facing.toYRot()));
        }

        float offset = (OUTLINE_SCALE - 1.0F) / 2.0F;
        Vector3f translation = new Vector3f(-offset, -offset, -offset);

        if (width != 1.0F) {
            translation.add(rotation.transform(new Vector3f(-(width - 1.0F) / 2.0F, 0.0F, 0.0F)));
        }

        return of(translation, rotation, new Vector3f(OUTLINE_SCALE * width, OUTLINE_SCALE, OUTLINE_SCALE));
    }

    public static Transformation item(BlockState state, float width) {
        Quaternionf rotation = new Quaternionf();

        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            rotation.rotateY((float) Math.toRadians(-facing.toYRot()));
        }

        float fill = state.getBlock() instanceof ChestBlock ? 16.0F / 14.0F : 1.0F;

        return of(
                new Vector3f(),
                rotation,
                new Vector3f(OUTLINE_SCALE * width * fill, OUTLINE_SCALE * fill, OUTLINE_SCALE * fill));
    }

    public static Transformation entity(Entity entity, AABB box) {
        AABB inflated = box.inflate(ENTITY_PADDING);
        Vec3 at = entity.position();
        return of(
                new Vector3f(
                        (float) (inflated.minX - at.x),
                        (float) (inflated.minY - at.y),
                        (float) (inflated.minZ - at.z)),
                new Quaternionf(),
                new Vector3f((float) inflated.getXsize(), (float) inflated.getYsize(), (float) inflated.getZsize()));
    }

    @Nullable
    public static Transformation segment(Vec3 start, Vec3 end) {
        return segment(start, end, 0.0D);
    }
    @Nullable
    public static Transformation segment(Vec3 start, Vec3 end, double trimmed) {
        Vec3 direction = end.subtract(start);
        double span = direction.length();
        float length = (float) (span - trimmed);

        if (span <= 0.0D || length <= 0.0F) {
            return null;
        }

        Vector3f axis = new Vector3f((float) direction.x, (float) direction.y, (float) direction.z).normalize();
        Quaternionf rotation = new Quaternionf().rotateTo(new Vector3f(0.0F, 0.0F, 1.0F), axis);
        Vector3f translation = rotation.transform(
                new Vector3f(-PATH_THICKNESS / 2.0F, -PATH_THICKNESS / 2.0F, 0.0F));
        translation.add(rotation.transform(new Vector3f(0.0F, 0.0F, (float) trimmed)));

        return of(translation, rotation, new Vector3f(PATH_THICKNESS, PATH_THICKNESS, length));
    }

    private static Transformation of(Vector3f translation, Quaternionf rotation, Vector3f scale) {
        return new Transformation(translation, rotation, scale, new Quaternionf());
    }
}
