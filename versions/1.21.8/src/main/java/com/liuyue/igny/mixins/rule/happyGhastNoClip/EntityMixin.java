package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.helper.happyGhastNoClip.NoClipHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "move", at = @At(value = "HEAD"))
    private void move(MoverType type, Vec3 movement, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (NoClipHelper.isActiveGhast(self)) {
            self.noPhysics = true;
        }
    }

    @Inject(method = "isInWall", at = @At(value = "HEAD"), cancellable = true)
    private void isInWall(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;

        if (NoClipHelper.isActiveGhastOrRider(self)) {
            cir.setReturnValue(false);
            return;
        }

        if (self instanceof Player player && !player.isPassenger() && NoClipHelper.isActive()) {
            List<HappyGhast> ghasts = player.level().getEntitiesOfClass(HappyGhast.class, player.getBoundingBox().inflate(1.0D));

            for (HappyGhast ghast : ghasts) {
                if (((HappyGhastInvoker) ghast).invokeScanPlayerAboveGhast()) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}
