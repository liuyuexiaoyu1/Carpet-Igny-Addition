package com.liuyue.igny.mixins.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import ml.mypals.ryansrenderingkit.render.MainRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MainRender.class)
@Pseudo
public class MainRenderMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"), require = 0)
    private static void translate(PoseStack instance, double d, double e, double f, Operation<Void> original) {
        return;
    }
}
