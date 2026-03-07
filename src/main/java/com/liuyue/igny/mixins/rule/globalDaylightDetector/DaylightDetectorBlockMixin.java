package com.liuyue.igny.mixins.rule.globalDaylightDetector;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import org.spongepowered.asm.mixin.injection.At;
//#if MC >= 12111
//$$ import com.llamalad7.mixinextras.sugar.Local;
//$$ import net.minecraft.world.attribute.EnvironmentAttribute;
//$$ import net.minecraft.world.attribute.EnvironmentAttributeSystem;
//#endif

@Mixin(DaylightDetectorBlock.class)
public class DaylightDetectorBlockMixin {
    @WrapOperation(method = "getTicker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasSkyLight()Z"))
    private static boolean hasSkyLight(DimensionType instance, Operation<Boolean> original) {
        if (IGNYSettings.globalDaylightDetector) {
            return true;
        }
        return original.call(instance);
    }

    //#if MC >= 26.1
    //$$ @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEffectiveSkyBrightness(Lnet/minecraft/core/BlockPos;)I"))
    //$$ private static int getEffectiveSkyBrightness(Level instance, BlockPos pos, Operation<Integer> original) {
    //#else
    @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBrightness(Lnet/minecraft/world/level/LightLayer;Lnet/minecraft/core/BlockPos;)I"))
    private static int getBrightness(Level instance, LightLayer lightLayer, BlockPos pos, Operation<Integer> original) {
        //#endif
        MinecraftServer server = instance.getServer();
        if (IGNYSettings.globalDaylightDetector && !instance.dimensionType().hasSkyLight() && server != null) {
            BlockPos.MutableBlockPos mutableCheckPos = new BlockPos.MutableBlockPos();
            mutableCheckPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            boolean isExposed = true;
            while (mutableCheckPos.getY() <= instance.getMaxBuildHeight()) {
                BlockState checkState = instance.getBlockState(mutableCheckPos);
                //#if MC >= 12102
                //$$ if (checkState.getLightBlock() >= 15 && !checkState.is(Blocks.BEDROCK)) {
                //#else
                if (checkState.getLightBlock(instance, mutableCheckPos) >= 15 && !checkState.is(Blocks.BEDROCK)) {
                    //#endif
                    isExposed = false;
                    break;
                }
                mutableCheckPos.move(Direction.UP);
            }
            if (!isExposed) return 0;
            instance = server.getLevel(Level.OVERWORLD);
            assert instance != null;
            //#if MC >= 26.1
            //$$ return original.call(instance, new BlockPos(pos.getX(), instance.getMaxY(), pos.getZ()));
            //#else
            return original.call(instance, lightLayer, new BlockPos(pos.getX(), instance.getMaxBuildHeight(), pos.getZ()));
            //#endif
        }
        //#if MC >= 26.1
        //$$ return original.call(instance, pos);
        //#else
        return original.call(instance, lightLayer, pos);
        //#endif
    }

    //#if MC < 26.1
    @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getSkyDarken()I"))
    private static int getSkyDarken(Level instance, Operation<Integer> original) {
        if (IGNYSettings.globalDaylightDetector) {
            MinecraftServer server = instance.getServer();
            if (IGNYSettings.globalDaylightDetector && server != null) {
                instance = server.getLevel(Level.OVERWORLD);
            }
        }
        return original.call(instance);
    }
    //#endif

    //#if MC >= 12111
    //$$  @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/core/BlockPos;)Ljava/lang/Object;"))
    //$$  private static Object getValue(EnvironmentAttributeSystem system, EnvironmentAttribute<?> environmentAttribute, BlockPos pos, Operation<Object> original, @Local(argsOnly = true) Level instance) {
    //#else
    @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getSunAngle(F)F"))
    private static float getSunAngle(Level instance, float f, Operation<Float> original) {
        //#endif
        if (IGNYSettings.globalDaylightDetector) {
            MinecraftServer server = instance.getServer();
            if (IGNYSettings.globalDaylightDetector && server != null) {
                //#if MC >= 12111
                //$$ Level level = server.getLevel(Level.OVERWORLD);
                //$$ if (level != null) {
                //$$ system = level.environmentAttributes();
                //$$ }
                //#else
                instance = server.getLevel(Level.OVERWORLD);
                //#endif
            }
        }
        //#if MC >= 12111
        //$$ return original.call(system, environmentAttribute, pos);
        //#else
        return original.call(instance, f);
        //#endif
    }
}
