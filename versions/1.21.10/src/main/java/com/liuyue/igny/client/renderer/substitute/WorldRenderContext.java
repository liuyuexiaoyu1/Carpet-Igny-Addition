package com.liuyue.igny.client.renderer.substitute;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;

public record WorldRenderContext(
        PoseStack poseStack,
        DeltaTracker deltaTracker,
        Camera camera,
        Frustum frustum,
        MultiBufferSource bufferSource
) {}