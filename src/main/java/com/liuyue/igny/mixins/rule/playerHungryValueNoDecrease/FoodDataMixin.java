package com.liuyue.igny.mixins.rule.playerHungryValueNoDecrease;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {
    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    private void addExhaustion(float f, CallbackInfo ci) {
        if (IGNYSettings.playerHungryValueNoDecrease) ci.cancel();
    }
}
