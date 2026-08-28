package com.liuyue.igny.mixins.rule.teleportInheritMinecartsMotionReintroduced;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(EndGatewayBlock.class)
public class EndGatewayBlockMixin {
    @WrapOperation(method = "getPortalDestination", at = @At(value = "NEW", target = "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FFLjava/util/Set;Lnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"))
    private TeleportTransition getPortalDestination(ServerLevel newLevel, Vec3 pos, Vec3 speed, float yRot, float xRot, Set<?> relatives, TeleportTransition.PostTeleportTransition postTeleportTransition, Operation<TeleportTransition> original, @Local(argsOnly = true) Entity entity) {
        if (IGNYSettings.TELEPORT_INHERIT_MINECARTS_MOTION_REINTRODUCED.value()) {
            return original.call(newLevel, pos, entity.getDeltaMovement(), yRot, xRot, relatives, postTeleportTransition);
        }
        return original.call(newLevel, pos, speed, yRot, xRot, relatives, postTeleportTransition);
    }
}
