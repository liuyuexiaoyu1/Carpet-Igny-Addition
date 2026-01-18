package com.liuyue.igny.mixins.rule.playerLowHungryValueCanSprint;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @WrapOperation(
            //#if MC >= 12111
            //$$ method = "isSprintingPossible", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEnoughFoodToDoExhaustiveManoeuvres()Z")
            //#else
            method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEnoughFoodToStartSprinting()Z")
            //#endif
    )
    private boolean sprint(LocalPlayer instance, Operation<Boolean> original) {
        if (IGNYSettings.playerLowHungryValueCanSprint) return true;
        return original.call(instance);
    }
}
