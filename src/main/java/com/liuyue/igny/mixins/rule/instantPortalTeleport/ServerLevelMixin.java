package com.liuyue.igny.mixins.rule.instantPortalTeleport;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(method = "addEntity", at = @At("RETURN"))
    private void addEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        if (!IGNYSettings.INSTANT_PORTAL_TELEPORT.value()) return;
        if (entity.isOnPortalCooldown()) return;
        ServerLevel level = (ServerLevel) entity.level();
        BlockPos pos = entity.blockPosition();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof Portal portal)) return;
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(portal, pos);
            ((EntityInvoker) entity).invokeHandlePortal();
        }
    }
}
