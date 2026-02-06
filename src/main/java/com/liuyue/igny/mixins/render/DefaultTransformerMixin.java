package com.liuyue.igny.mixins.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import ml.mypals.ryansrenderingkit.transform.shapeTransformers.DefaultTransformer;
import ml.mypals.ryansrenderingkit.transform.shapeTransformers.TransformLayer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DefaultTransformer.class)
@Pseudo
public class DefaultTransformerMixin {
    @Shadow
    @Final
    public TransformLayer world;

    @WrapOperation(method = "applyLayer", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"), require = 0)
    private void translate(PoseStack instance, double d, double e, double f, Operation<Void> original, @Local(argsOnly = true) TransformLayer layer){
        if (layer.equals(this.world)) {
            Minecraft mc = Minecraft.getInstance();
            Camera camera = mc.gameRenderer.getMainCamera();
            //#if MC >= 12111
            //$$ Vec3 cameraPos = camera.position();
            //#else
            Vec3 cameraPos = camera.getPosition();
            //#endif
            original.call(instance, d - cameraPos.x, e - cameraPos.y, f - cameraPos.z);
            return;
        }
        original.call(instance, d, e, f);
    }
}
