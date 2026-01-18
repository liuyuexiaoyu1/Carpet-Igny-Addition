package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Player.class,priority = 1100)
public class PlayerMixin {
    @WrapOperation(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z"))
    private boolean isSpectatorWrap(Player instance, Operation<Boolean> original) {
        return original.call(instance) || instance.getVehicle() instanceof HappyGhast&& IGNYSettings.happyGhastNoClip;
    }

    @WrapOperation(method = "aiStep", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z")
    )
    private boolean collidesWithEntities(Player instance,Operation<Boolean> original)
    {
        return original.call(instance) || instance.getVehicle() instanceof HappyGhast&& IGNYSettings.happyGhastNoClip;
    }

    @WrapOperation(method = "updatePlayerPose", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z")
    )
    private boolean spectatorsDontPose(Player instance,Operation<Boolean> original)
    {
        return original.call(instance) || instance.getVehicle() instanceof HappyGhast&& IGNYSettings.happyGhastNoClip;
    }
}
