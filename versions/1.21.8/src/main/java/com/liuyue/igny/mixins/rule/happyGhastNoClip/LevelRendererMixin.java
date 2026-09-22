package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.helper.happyGhastNoClip.NoClipHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
//?>= 26.2 ? import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LevelRenderer.class, priority = 1100) //#replace >= 26.2 ? @Mixin(value = Camera.class, priority = 1100)
public class LevelRendererMixin {
    //#if >= 26.1
    //#if >= 26.2
    //$$ @WrapOperation(method = "extractRenderState",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"))
    //#else
    //$$ @WrapOperation(method = "update", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"))
    //#endif
    //$$  private boolean isSpectatorWrap(LocalPlayer instance, Operation<Boolean> original) {
    //#else
    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"))
    private boolean isSpectator(LocalPlayer instance, Operation<Boolean> original) {
        //#endif
        return original.call(instance) || NoClipHelper.isActiveRider(instance);
    }
}
