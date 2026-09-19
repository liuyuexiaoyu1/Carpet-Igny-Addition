package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.helper.happyGhastNoClip.NoClipHelper;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Inject(method = "noBlocksAround", at = @At("HEAD"), cancellable = true)
    private void noBlocksAround(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (NoClipHelper.isActiveGhast(entity)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isEntityCollidingWithAnythingNew", at = @At("HEAD"), cancellable = true)
    private void isEntityCollidingWithAnythingNew(LevelReader level, Entity entity, AABB box, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (NoClipHelper.isActiveGhastOrRider(entity)) {
            cir.setReturnValue(false);
        }
    }
}
