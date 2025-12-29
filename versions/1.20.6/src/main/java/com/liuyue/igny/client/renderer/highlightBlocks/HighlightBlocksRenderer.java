package com.liuyue.igny.client.renderer.highlightBlocks;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.*;

//#if MC < 12005
//$$ import org.joml.Matrix4f;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class HighlightBlocksRenderer {

    private static final Map<BlockPos, HighlightEntry> HIGHLIGHTS = new HashMap<>();

    public static void init() {
        WorldRenderEvents.LAST.register(HighlightBlocksRenderer::onWorldRender);
    }

    public static void addHighlight(BlockPos pos, int argbColor, int durationTicks, boolean seeThrough, boolean permanent) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        long expireTime = mc.level.getGameTime() + durationTicks;
        if (permanent) expireTime = Long.MAX_VALUE;
        HIGHLIGHTS.put(pos.immutable(), new HighlightEntry(argbColor, expireTime, seeThrough));
    }

    public static void removeHighlight(BlockPos pos){
        HIGHLIGHTS.remove(pos.immutable());
    }

    private static void onWorldRender(WorldRenderContext context) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || HIGHLIGHTS.isEmpty()) return;

        long now = mc.level.getGameTime();
        HIGHLIGHTS.entrySet().removeIf(entry -> entry.getValue().expireTime <= now);
        if (HIGHLIGHTS.isEmpty()) return;


        Vec3 cameraPos = context.camera().getPosition();
        PoseStack poseStack = context.matrixStack();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        BufferBuilder vc = Tesselator.getInstance().getBuilder();
        vc.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_COLOR);

        if (poseStack != null) {
            poseStack.pushPose();
            poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
            for (var entry : HIGHLIGHTS.entrySet()) {
                BlockPos pos = entry.getKey();
                HighlightEntry data = entry.getValue();
                Vec3 offset = Vec3.atCenterOf(pos).subtract(cameraPos);
                int renderDistance = mc.options.renderDistance().get() * 16;
                Vec3 correction = new Vec3(offset.x(), offset.y(), offset.z());
                if (correction.length() > renderDistance) continue;

                float a = ((data.color >> 24) & 0xFF) / 255.0f;
                float r = ((data.color >> 16) & 0xFF) / 255.0f;
                float g = ((data.color >> 8) & 0xFF) / 255.0f;
                float b = (data.color & 0xFF) / 255.0f;

                poseStack.pushPose();
                poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
                //#if MC < 12005
                //$$ Matrix4f mat = poseStack.last().pose();
                //$$ renderFilledCube(mat, bufferBuilder, r, g, b, a);
                //#else
                renderFilledCube(bufferBuilder, pos.getX() - cameraPos.x(), pos.getY() - cameraPos.y(), pos.getZ() - cameraPos.z(), r, g, b, a);
                //#endif
                poseStack.popPose();
            }
            poseStack.popPose();
            try {
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                GlStateManager._disableDepthTest();
                GlStateManager._enableBlend();
                GlStateManager._disableCull();

                BufferBuilder.RenderedBuffer meshData = vc.end();
                BufferUploader.drawWithShader(meshData);

                GlStateManager._enableDepthTest();
                GlStateManager._disableBlend();
                GlStateManager._enableCull();
            } catch (Exception ignored) {}
        }
    }

    //#if MC < 12005
    //$$ private static void renderFilledCube(Matrix4f matrix, BufferBuilder bufferBuilder, float r, float g, float b, float a) {
    //$$     float min = 0.0f; float max = 1.0f;
    //$$     quad(matrix, bufferBuilder, min, max, min, max, max, min, max, max, max, min, max, max, r, g, b, a);
    //$$     quad(matrix, bufferBuilder, min, min, max, max, min, max, max, min, min, min, min, min, r, g, b, a);
    //$$     quad(matrix, bufferBuilder, max, min, min, max, min, max, max, max, max, max, max, min, r, g, b, a);
    //$$     quad(matrix, bufferBuilder, min, min, max, min, min, min, min, max, min, min, max, max, r, g, b, a);
    //$$     quad(matrix, bufferBuilder, min, min, max, max, min, max, max, max, max, min, max, max, r, g, b, a);
    //$$     quad(matrix, bufferBuilder, max, min, min, min, min, min, min, max, min, max, max, min, r, g, b, a);
    //$$ }
    //$$ private static void quad(Matrix4f matrix, BufferBuilder bufferBuilder, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float r, float g, float b, float a) {
    //$$     bufferBuilder.vertex(matrix, x1, y1, z1).color(r, g, b, a).endVertex();
    //$$     bufferBuilder.vertex(matrix, x2, y2, z2).color(r, g, b, a).endVertex();
    //$$     bufferBuilder.vertex(matrix, x3, y3, z3).color(r, g, b, a).endVertex();
    //$$     bufferBuilder.vertex(matrix, x4, y4, z4).color(r, g, b, a).endVertex();
    //$$ }
    //#else
    private static void renderFilledCube(BufferBuilder bufferBuilder, double x, double y, double z, float r, float g, float b, float a) {
        float min = 0.0f; float max = 1.0f;
        quad(bufferBuilder, x, y, z, min, max, min, max, max, min, max, max, max, min, max, max, r, g, b, a);
        quad(bufferBuilder, x, y, z, min, min, max, max, min, max, max, min, min, min, min, min, r, g, b, a);
        quad(bufferBuilder, x, y, z, max, min, min, max, min, max, max, max, max, max, max, min, r, g, b, a);
        quad(bufferBuilder, x, y, z, min, min, max, min, min, min, min, max, min, min, max, max, r, g, b, a);
        quad(bufferBuilder, x, y, z, min, min, max, max, min, max, max, max, max, min, max, max, r, g, b, a);
        quad(bufferBuilder, x, y, z, max, min, min, min, min, min, min, max, min, max, max, min, r, g, b, a);
    }
    private static void quad(BufferBuilder bufferBuilder, double x, double y, double z, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float r, float g, float b, float a) {
        bufferBuilder.vertex(x + x1, y + y1, z + z1).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(x + x2, y + y2, z + z2).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(x + x3, y + y3, z + z3).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(x + x4, y + y4, z + z4).color(r, g, b, a).endVertex();
    }
    //#endif

    private record HighlightEntry(int color, long expireTime, boolean seeThrough) {}
}