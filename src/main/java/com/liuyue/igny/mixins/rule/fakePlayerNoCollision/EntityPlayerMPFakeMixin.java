package com.liuyue.igny.mixins.rule.fakePlayerNoCollision;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.fakePlayerNoCollision.NoCollisionFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMPFake.class)
public class EntityPlayerMPFakeMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void igny$syncNoCollisionFlag(CallbackInfo ci) {
        if (((Object) this) instanceof NoCollisionFlag flag) {
            flag.igny$setNoCollision(IGNYSettings.FAKE_PLAYER_NO_COLLISION.value());
        }
    }
}
