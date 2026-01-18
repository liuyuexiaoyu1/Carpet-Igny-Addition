package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.animal.HappyGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LevelRenderer.class, priority = 1100)
public class LevelRendererMixin {
    @WrapOperation(method = "renderLevel",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"))
    private boolean isSpectatorWrap(LocalPlayer instance, Operation<Boolean> original) {
        return original.call(instance) || instance.getVehicle() instanceof HappyGhast&& IGNYSettings.happyGhastNoClip;
    }

}
