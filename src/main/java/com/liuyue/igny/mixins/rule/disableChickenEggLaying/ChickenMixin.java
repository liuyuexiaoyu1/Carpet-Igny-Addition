package com.liuyue.igny.mixins.rule.disableChickenEggLaying;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chicken.class)
public class ChickenMixin {
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Chicken;isAlive()Z"), cancellable = true)
    private void aiStep(CallbackInfo ci) {
        if (IGNYSettings.DISABLE_CHICKEN_EGG_LAYING.value()) {
            ci.cancel();
        }
    }
}
