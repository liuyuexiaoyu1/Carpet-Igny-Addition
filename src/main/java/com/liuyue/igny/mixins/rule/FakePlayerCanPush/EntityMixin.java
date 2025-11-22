package com.liuyue.igny.mixins.rule.FakePlayerCanPush;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void onPush(Entity entity, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;
        if (!IGNYSettings.FakePlayerCanPush) {
            if (self instanceof EntityPlayerMPFake || entity instanceof EntityPlayerMPFake) {
                ci.cancel();
            }
        }
    }
}
