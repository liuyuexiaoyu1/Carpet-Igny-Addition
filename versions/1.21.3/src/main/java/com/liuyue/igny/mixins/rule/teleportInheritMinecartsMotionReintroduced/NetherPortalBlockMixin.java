package com.liuyue.igny.mixins.rule.teleportInheritMinecartsMotionReintroduced;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {
    @WrapOperation(method = "createDimensionTransition",at = @At(value = "NEW", target = "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FFLjava/util/Set;Lnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"))
    private static TeleportTransition createDimensionTransition(ServerLevel serverLevel, Vec3 vec3, Vec3 vec32, float f, float g, Set<?> set, TeleportTransition.PostTeleportTransition postTeleportTransition, Operation<TeleportTransition> original, @Local(argsOnly = true) Direction.Axis axis, @Local(ordinal = 1) Direction.Axis axis2, @Local(argsOnly = true) Entity entity) {
        if (IGNYSettings.teleportInheritMinecartsMotionReintroduced) {
            Vec3 vec3d3 = axis == axis2 ? entity.getDeltaMovement() : new Vec3(entity.getDeltaMovement().z, entity.getDeltaMovement().y, -entity.getDeltaMovement().x);
            return original.call(serverLevel, vec3, vec3d3, 0f, g, set, postTeleportTransition);
        }
        return original.call(serverLevel, vec3, vec32, f, g, set, postTeleportTransition);
    }
}