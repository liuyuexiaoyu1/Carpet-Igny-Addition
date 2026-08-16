package com.liuyue.igny.helper.betterEasyPlaceProtocol;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EasyPlaceExtraProtocolHelper {
    public static boolean isProtocol(double relativeHitDim) {
        return relativeHitDim >= (double) 2.0F;
    }

    public static boolean isExtraProtocol(int protocolValue) {
        return (protocolValue & 0b0000_1000) == 0b0000_1000;
    }

    public static double getRelativeHitX(Vec3 hitPos, BlockPos blockPos) {
        return hitPos.x - (double) blockPos.getX();
    }

    public static double getRelativeHitZ(Vec3 hitPos, BlockPos blockPos) {
        return hitPos.z - (double) blockPos.getZ();
    }

    public static int decodeProtocolValueFromHitDim(double relativeHitDim) {
        return ((int) relativeHitDim - 2) >>> 1;
    }

    public static double encodeProtocolValueToHitDim(double relativeHitDim, int protocolValue) {
        return relativeHitDim + (double) ((protocolValue << 1) + 2);
    }

    public static int extraProtocolValueToRawProtocolValue(int protocolValue) {
        return ((protocolValue & 0b1111_0000) >>> 1) | (protocolValue & 0b0000_0111);
    }

    public static int rawProtocolValueToExtraProtocolValue(int protocolValue) {
        return ((protocolValue & 0b0111_1000) << 1) | (protocolValue & 0b0000_0111) | 0b0000_1000;
    }

    public static int addExtraProtocolBit(int protocolValue) {
        return protocolValue | 0b0000_1000;
    }

    public static int removeExtraProtocolBit(int protocolValue) {
        return protocolValue & ~((int) 0b0000_1000);
    }

    public static Vec3 encodeProtocolValueToHitVecX(int protocolValue, Vec3 hitVec) {
        return new Vec3(encodeProtocolValueToHitDim(hitVec.x, protocolValue), hitVec.y, hitVec.z);
    }

    public static Vec3 encodeProtocolValueToHitVecZ(int protocolAdditionValue, Vec3 hitVec) {
        return new Vec3(hitVec.x, hitVec.y, encodeProtocolValueToHitDim(hitVec.z, protocolAdditionValue));
    }

    public static Vec3 encodeExtraProtocolValueToHitVecX(int protocolValue, Vec3 hitVec) {
        return encodeProtocolValueToHitVecX(rawProtocolValueToExtraProtocolValue(protocolValue), hitVec);
    }

    public static @Nullable Property<Direction> getFirstDirectionProperty(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (Direction.class.isAssignableFrom(property.getValueClass())) {
                @SuppressWarnings("unchecked")
                Property<Direction> directionProperty = (Property<Direction>) property;
                return directionProperty;
            }
        }
        return null;
    }
}