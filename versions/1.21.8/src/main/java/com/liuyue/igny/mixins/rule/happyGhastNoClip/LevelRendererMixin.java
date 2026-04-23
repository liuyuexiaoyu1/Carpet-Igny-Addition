package com.liuyue.igny.mixins.rule.happyGhastNoClip;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
//#if MC >= 26.2
//$$ import net.minecraft.client.Camera;
//#endif
import net.minecraft.world.entity.animal.HappyGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 26.2
//$$ @Mixin(value = Camera.class, priority = 1100)
//#else
@Mixin(value = LevelRenderer.class, priority = 1100)
//#endif
public class LevelRendererMixin {
    //#if MC >= 26.1
    //#if MC >= 26.2
    //$$ @WrapOperation(method = "extractRenderState",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"))
    //#else
    //$$ @WrapOperation(method = "update", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"))
    //#endif
    //$$  private boolean isSpectatorWrap(LocalPlayer instance, Operation<Boolean> original) {
    //#else
    @WrapOperation(method = "renderLevel",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"))
    private boolean isSpectatorWrap(LocalPlayer instance, Operation<Boolean> original) {
        //#endif
        return original.call(instance) || instance.getVehicle() instanceof HappyGhast && IGNYSettings.happyGhastNoClip;
    }
}
