package com.liuyue.igny.mixins.rule.instantPortalTeleport;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
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
        if (!state.is(Blocks.NETHER_PORTAL) && !state.is(Blocks.END_PORTAL)) return;
        EntityInvoker invoker = (EntityInvoker) entity;
        if (entity.canChangeDimensions()) {
            entity.handleInsidePortal(pos);
            if (state.is(Blocks.NETHER_PORTAL)) {
                invoker.igny$invokeHandleNetherPortal();
            }
        }
    }
}
