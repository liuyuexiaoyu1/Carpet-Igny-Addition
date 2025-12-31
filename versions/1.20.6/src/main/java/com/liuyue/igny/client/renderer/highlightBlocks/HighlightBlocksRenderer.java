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
import org.joml.Matrix4f;

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

    public static void addHighlight(BlockPos pos, int argbColor, int durationTicks, boolean permanent) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        long expireTime = mc.level.getGameTime() + durationTicks;
        if (permanent) expireTime = Long.MAX_VALUE;
        HIGHLIGHTS.put(pos.immutable(), new HighlightEntry(argbColor, expireTime));
    }

    public static void removeHighlight(BlockPos pos){
        HIGHLIGHTS.remove(pos.immutable());
    }
    public static void clearHighlight() {HIGHLIGHTS.clear();}

    private static void onWorldRender(WorldRenderContext context) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || HIGHLIGHTS.isEmpty()) return;

        long now = mc.level.getGameTime();
        HIGHLIGHTS.entrySet().removeIf(entry -> entry.getValue().expireTime <= now);
        if (HIGHLIGHTS.isEmpty()) return;


        Vec3 cameraPos = context.camera().getPosition();
        PoseStack poseStack = context.matrixStack();
        BufferBuilder vc = Tesselator.getInstance().getBuilder();
        vc.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_COLOR);

        if (poseStack != null) {
            for (var entry : HIGHLIGHTS.entrySet()) {
                BlockPos pos = entry.getKey();
                HighlightEntry data = entry.getValue();

                double blockX = pos.getX() + 0.5;
                double blockY = pos.getY() + 0.5;
                double blockZ = pos.getZ() + 0.5;

                double dx = blockX - cameraPos.x;
                double dy = blockY - cameraPos.y;
                double dz = blockZ - cameraPos.z;

                if (Math.sqrt(dx * dx + dz * dz) > mc.options.renderDistance().get() * 16) continue;

                float a = ((data.color >> 24) & 0xFF) / 255.0f;
                float r = ((data.color >> 16) & 0xFF) / 255.0f;
                float g = ((data.color >> 8) & 0xFF) / 255.0f;
                float b = (data.color & 0xFF) / 255.0f;

                //#if MC < 12005
                //$$ Matrix4f viewProj = poseStack.last().pose();
                //$$ Matrix4f model = new Matrix4f();
                //$$ model.translation((float) dx, (float) dy, (float) dz);
                //$$ Matrix4f finalMat = new Matrix4f(viewProj);
                //$$ finalMat.mul(model);
                //$$ renderFilledCube(finalMat, vc, r, g, b, a);
                //#else
                renderFilledCube(vc, dx, dy, dz, r, g, b, a);
                //#endif
            }
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
    //$$     float min = -0.5f; float max = 0.5f;
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
        float min = -0.5f; float max = 0.5f;
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

    private record HighlightEntry(int color, long expireTime) {}
}