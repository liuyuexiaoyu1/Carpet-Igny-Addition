package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.helper.happyGhastNoClip.NoClipHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.HappyGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HappyGhast.class)
public abstract class HappyGhastMixin {

    @Inject(method = "canBeCollidedWith", at = @At(value = "HEAD"), cancellable = true)
    private void canBeCollidedWith(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (NoClipHelper.isActiveGhast((HappyGhast) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}
