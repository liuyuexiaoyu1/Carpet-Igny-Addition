package com.liuyue.igny.mixins.rule.globalDaylightDetector;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer; //?< 26.1
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
//#if >= 1.21.11
/*$$import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;$$*/
//#endif

@Mixin(DaylightDetectorBlock.class)
public class DaylightDetectorBlockMixin {
    @Unique
    private static final int MAX_LIGHT = 15;

    @WrapOperation(method = "getTicker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasSkyLight()Z"))
    private static boolean hasSkyLight(DimensionType instance, Operation<Boolean> original) {
        if (IGNYSettings.GLOBAL_DAYLIGHT_DETECTOR.value()) {
            return true;
        }
        return original.call(instance);
    }

    //#if >= 26.1
    /*$$
    @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEffectiveSkyBrightness(Lnet/minecraft/core/BlockPos;)I"))
    private static int getEffectiveSkyBrightness(Level instance, BlockPos pos, Operation<Integer> original) {
        if (!IGNYSettings.GLOBAL_DAYLIGHT_DETECTOR.value() || instance.dimensionType().hasSkyLight()) {
            return original.call(instance, pos);
        }

        Level overworld = overworldOf(instance);
        if (overworld == null) {
            return 0;
        }

        return Math.max(MAX_LIGHT - overworld.getSkyDarken() - lightLostBelow(instance, pos), 0);
    }$$*/
    //#else
    @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBrightness(Lnet/minecraft/world/level/LightLayer;Lnet/minecraft/core/BlockPos;)I"))
    private static int getBrightness(Level instance, LightLayer lightLayer, BlockPos pos, Operation<Integer> original) {
        if (!IGNYSettings.GLOBAL_DAYLIGHT_DETECTOR.value() || instance.dimensionType().hasSkyLight()) {
            return original.call(instance, lightLayer, pos);
        }

        return Math.max(MAX_LIGHT - lightLostBelow(instance, pos), 0);
    }
    //#endif

    @Unique
    private static int lightLostBelow(Level level, BlockPos pos) {
        int maxY = level.getMaxBuildHeight(); //#replace >= 26.1 ? int maxY = level.getMaxY();
        int lost = 0;

        for (int y = pos.getY() + 1; y <= maxY; y++) {
            BlockPos scan = new BlockPos(pos.getX(), y, pos.getZ());
            BlockState state = level.getBlockState(scan);
            //#if >= 26.1
            //$$ int dampening = state.getLightDampening();
            //#elseif >= 1.21.2
            //$$ int dampening = state.getLightBlock();
            //#else
            int dampening = state.getLightBlock(level, scan);
            //#endif
            if (dampening > 0) {
                lost += dampening;
                if (lost >= MAX_LIGHT) {
                    return MAX_LIGHT;
                }
            }
        }

        return lost;
    }

    @Unique
    private static Level overworldOf(Level level) {
        MinecraftServer server = level.getServer();
        return server == null ? null : server.getLevel(Level.OVERWORLD);
    }

    //#if < 26.1
    @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getSkyDarken()I"))
    private static int getSkyDarken(Level instance, Operation<Integer> original) {
        if (!IGNYSettings.GLOBAL_DAYLIGHT_DETECTOR.value() || instance.dimensionType().hasSkyLight()) {
            return original.call(instance);
        }

        Level overworld = overworldOf(instance);
        return overworld == null ? 0 : overworld.getSkyDarken();
    }
    //#endif

    //#if >= 1.21.11
    /*$$
    @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/core/BlockPos;)Ljava/lang/Object;"))
    private static Object getSunAngle(EnvironmentAttributeSystem system, EnvironmentAttribute<?> attribute, BlockPos pos,
                                      Operation<Object> original, @Local(argsOnly = true) Level instance) {
        if (!IGNYSettings.GLOBAL_DAYLIGHT_DETECTOR.value() || instance.dimensionType().hasSkyLight()) {
            return original.call(system, attribute, pos);
        }

        Level overworld = overworldOf(instance);
        return overworld == null
                ? original.call(system, attribute, pos)
                : original.call(overworld.environmentAttributes(), attribute, pos);
    }$$*/
    //#else
    @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getSunAngle(F)F"))
    private static float getSunAngle(Level instance, float partialTick, Operation<Float> original) {
        if (!IGNYSettings.GLOBAL_DAYLIGHT_DETECTOR.value() || instance.dimensionType().hasSkyLight()) {
            return original.call(instance, partialTick);
        }

        Level overworld = overworldOf(instance);
        return overworld == null ? 0.0F : original.call(overworld, partialTick);
    }
    //#endif
}
