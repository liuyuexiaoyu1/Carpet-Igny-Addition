package com.liuyue.igny.mixins.carpet.fix.FakePlayerBoatYawFix;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",at= @At(value = "RETURN"))
    private void startRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.fakePlayerBoatYawFix && cir.getReturnValueZ()) {
            Entity self = (Entity) (Object) this;
            if (self instanceof EntityPlayerMPFake && entity instanceof Boat) {
                self.yRotO = entity.getYRot();
                self.setYRot(entity.getYRot());
                self.setYHeadRot(entity.getYRot());
            }
        }
}
}
