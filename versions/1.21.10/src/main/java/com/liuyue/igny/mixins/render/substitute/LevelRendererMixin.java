package com.liuyue.igny.mixins.render.substitute;

import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.client.renderer.substitute.WorldRenderContext;
import com.liuyue.igny.client.renderer.substitute.WorldRenderEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    @Final
    private LevelTargetBundle targets;

    @Unique
    private WorldRenderContext context;

    @Unique
    private PoseStack poseStack;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void onRender(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean bl, Camera camera, Matrix4f matrix4f, Matrix4f matrix4f2, Matrix4f matrix4f3, GpuBufferSlice gpuBufferSlice, Vector4f vector4f, boolean bl2, CallbackInfo ci){
        WorldRenderEvents.START.invoker().onStart();
    }

    @WrapOperation(
            //#if MC >= 26.1
            //$$ method = "lambda$addMainPass$0",
            //#else
            method = "method_62214",
            //#endif
            at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    private PoseStack setPoseStack(Operation<PoseStack> original){
        PoseStack result = original.call();
        this.poseStack = result;
        return result;
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/framegraph/FramePass;executes(Ljava/lang/Runnable;)V"))
    private void setContext(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean bl, Camera camera, Matrix4f matrix4f, Matrix4f matrix4f2, Matrix4f matrix4f3, GpuBufferSlice gpuBufferSlice, Vector4f vector4f, boolean bl2, CallbackInfo ci, @Local Frustum frustum){
        this.context = new WorldRenderContext(this.poseStack, deltaTracker, camera, frustum, this.renderBuffers.bufferSource());
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getCloudsType()Lnet/minecraft/client/CloudStatus;"))
    private void afterTranslucent(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean bl, Camera camera, Matrix4f matrix4f, Matrix4f matrix4f2, Matrix4f matrix4f3, GpuBufferSlice gpuBufferSlice, Vector4f vector4f, boolean bl2, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder){
        FramePass pass = frameGraphBuilder.addPass(IGNYServer.MOD_ID + ":onAfterTranslucent");
        this.targets.main = pass.readsAndWrites(this.targets.main);
        pass.executes(() -> WorldRenderEvents.AFTER_TRANSLUCENT.invoker().render(this.context));
    }

    @Inject(
            //#if MC >= 26.1
            //$$ method = "lambda$addMainPass$0",
            //#else
            method = "method_62214",
            //#endif
            at = @At(value = "INVOKE",
            //#if MC >= 12111
            //$$ target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures()V"
            //#else
            target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/culling/Frustum;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDDZ)V"
            //#endif
    ))
    private void onDebug(GpuBufferSlice gpuBufferSlice, LevelRenderState levelRenderState, ProfilerFiller profilerFiller, Matrix4f matrix4f, ResourceHandle<?> resourceHandle, ResourceHandle<?> resourceHandle2, boolean bl,
                         //#if MC < 12111
                         Frustum frustum,
                         //#endif
                         ResourceHandle<?> resourceHandle3, ResourceHandle<?> resourceHandle4, CallbackInfo ci){
        WorldRenderEvents.BEFORE_DEBUG_RENDER.invoker().render(this.context);
    }
}
