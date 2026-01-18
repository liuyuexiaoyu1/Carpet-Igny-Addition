package com.liuyue.igny.mixins.rule.fakePlayerCanPush;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void onPush(Entity entity, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;
        if (!IGNYSettings.fakePlayerCanPush) {
            if (self instanceof EntityPlayerMPFake || entity instanceof EntityPlayerMPFake) {
                ci.cancel();
            }
        }
    }
}
