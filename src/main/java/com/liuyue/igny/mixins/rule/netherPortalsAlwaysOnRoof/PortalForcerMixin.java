package com.liuyue.igny.mixins.rule.netherPortalsAlwaysOnRoof;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PortalForcer.class)
public class PortalForcerMixin {
    @Unique
    private static final int ROOF_Y = 128;

    @Shadow
    @Final
    private ServerLevel level;

    @Unique
    private boolean shouldForceRoof() {
        return IGNYSettings.NETHER_PORTALS_ALWAYS_ON_ROOF.value() && this.level.dimension().equals(Level.NETHER);
    }

    @WrapOperation(
            method = "createPortal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"
            )
    )
    private int getHeight(ServerLevel instance, Heightmap.Types types, int i, int j, Operation<Integer> original) {
        int height = original.call(instance, types, i, j);
        if (this.shouldForceRoof()) {
            return Math.max(height, ROOF_Y);
        }
        return height;
    }

    @WrapOperation(
            method = "createPortal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getMinBuildHeight()I"
            )
    )
    private int getMinY(ServerLevel instance, Operation<Integer> original) {
        if (this.shouldForceRoof()) {
            return ROOF_Y;
        }
        return original.call(instance);
    }

    @WrapOperation(
            method = "createPortal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Mth;clamp(III)I"
            )
    )
    private int wrapClamp(int value, int min, int max, Operation<Integer> original) {
        if (this.shouldForceRoof()) {
            int newMin = Math.max(min, ROOF_Y);
            if (newMin > max) {
                return max;
            }
            return original.call(value, newMin, max);
        }
        return original.call(value, min, max);
    }
}
