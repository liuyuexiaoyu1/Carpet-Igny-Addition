package com.liuyue.igny.mixins.carpet.fix.FakePlayerBoatYawFix;

//#if MC < 12111
import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#endif

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class EntityMixin {
    //#if MC < 12111
    //#if MC >= 12109
    //$$ @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z",at= @At(value = "RETURN"))
    //$$ private void startRiding(Entity entity, boolean bl, boolean bl2, CallbackInfoReturnable<Boolean> cir) {
    //#else
    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",at= @At(value = "RETURN"))
    private void startRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
    //#endif
        if (IGNYSettings.fakePlayerBoatYawFix && cir.getReturnValueZ()) {
            Entity self = (Entity) (Object) this;
            if (self instanceof EntityPlayerMPFake && entity instanceof Boat) {
                self.setYRot(entity.getYRot());
                self.setYHeadRot(entity.getYRot());
            }
        }
    }
    //#endif
}
