package com.liuyue.igny.mixins.features.rule.minecartMotionFix;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Relative;
import net.minecraft.BlockUtil;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {

    @Inject(method = "createDimensionTransition",at = @At("RETURN"),cancellable = true)
    private static void createDimensionTransition(ServerLevel serverLevel, BlockUtil.FoundRectangle foundRectangle, Direction.Axis axis, Vec3 vec3, Entity entity, TeleportTransition.PostTeleportTransition postTeleportTransition, CallbackInfoReturnable<TeleportTransition> cir) {
        if (IGNYSettings.teleportInheritMinecartsMotionReintroduced) {
            BlockPos blockPos = foundRectangle.minCorner;
            BlockState blockState = serverLevel.getBlockState(blockPos);
            Direction.Axis axis2 = blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
            double d = foundRectangle.axis1Size;
            double e = foundRectangle.axis2Size;
            EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
            Vec3 vec3d3 = axis == axis2 ? entity.getDeltaMovement() : new Vec3(entity.getDeltaMovement().z, entity.getDeltaMovement().y,
                    -entity.getDeltaMovement().x);
            double f = (double) entityDimensions.width() / 2.0 + (d - (double) entityDimensions.width()) * vec3.x();
            double g = (e - (double) entityDimensions.height()) * vec3.y();
            double h = 0.5 + vec3.z();
            boolean bl = axis2 == Direction.Axis.X;
            Vec3 vec3d = new Vec3((double) blockPos.getX() + (bl ? f : h), (double) blockPos.getY() + g, (double) blockPos.getZ() + (bl ? h : f));
            Vec3 vec3d2 = PortalShape.findCollisionFreePosition(vec3d, serverLevel, entity, entityDimensions);
            cir.setReturnValue(new TeleportTransition(
                    serverLevel, vec3d2, vec3d3, 0, 0.0F,
                    Relative.union(Relative.DELTA, Relative.ROTATION),
                    postTeleportTransition
            ));
        }
    }
}