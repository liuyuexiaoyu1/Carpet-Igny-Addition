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
            //#if MC >= 12110
            //$$ method = "isSprintingPossible", at = @At(value = "INVOKE",
            //#if MC >= 12111
            //$$ target = "Lnet/minecraft/client/player/LocalPlayer;hasEnoughFoodToDoExhaustiveManoeuvres()Z"
            //#else
            //$$ target = "Lnet/minecraft/client/player/LocalPlayer;hasEnoughFoodToSprint()Z"
            //#endif
            //$$ )
            //#else
            method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEnoughFoodToStartSprinting()Z")
            //#endif
    )
    private boolean sprint(LocalPlayer instance, Operation<Boolean> original) {
        if (IGNYSettings.PLAYER_LOW_HUNGRY_VALUE_CAN_SPRINT.value()) return true;
        return original.call(instance);
    }
}
