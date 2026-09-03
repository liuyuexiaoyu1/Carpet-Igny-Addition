package com.liuyue.igny.mixins.rule.instantPortalTeleport;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if MC >= 12101
import net.minecraft.world.level.block.Portal;
//#else
//$$ import net.minecraft.world.level.block.Blocks;
//$$ import net.minecraft.world.level.block.state.BlockState;
//#endif

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "addEntity", at = @At(value = "RETURN"))
    private void addEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!IGNYSettings.INSTANT_PORTAL_TELEPORT.value()) return;
        if (entity.isOnPortalCooldown()) return;

        ServerLevel level = (ServerLevel) entity.level();
        BlockPos portalPos = EntityUtil.findPortalInBoundingBox(level, entity.getBoundingBox());
        if (portalPos == null) return;
        //#if MC >= 12101
        if (level.getBlockState(portalPos).getBlock() instanceof Portal portal && entity.canUsePortal(false)) {
            entity.setAsInsidePortal(portal, portalPos);
            ((EntityInvoker) entity).invokeHandlePortal();
        }
        //#else
        //$$ BlockState state = level.getBlockState(portalPos);
        //$$ if (!state.is(Blocks.NETHER_PORTAL) && !state.is(Blocks.END_PORTAL)) return;
        //$$ EntityInvoker invoker = (EntityInvoker) entity;
        //$$ if (entity.canChangeDimensions()) {
        //$$     entity.handleInsidePortal(portalPos);
        //$$     if (state.is(Blocks.NETHER_PORTAL)) {
        //$$         invoker.igny$invokeHandleNetherPortal();
        //$$     }
        //$$ }
        //#endif
    }
}
