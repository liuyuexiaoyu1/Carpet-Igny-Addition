package com.liuyue.igny.client.renderer.highlightBlocks;

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

    public static void addHighlight(BlockPos pos, int argbColor, int durationTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        HIGHLIGHTS.put(pos.immutable(), new HighlightEntry(argbColor, mc.level.getGameTime() + durationTicks));
    }

    private static void onWorldRender(WorldRenderContext context) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || HIGHLIGHTS.isEmpty()) return;

        long now = mc.level.getGameTime();
        HIGHLIGHTS.entrySet().removeIf(entry -> entry.getValue().expireTime <= now);
        if (HIGHLIGHTS.isEmpty()) return;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Vec3 cameraPos = context.camera().getPosition();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        int renderDistance = mc.options.renderDistance().get() * 16;
        double renderDistanceSq = renderDistance * renderDistance;
        Vec3 playerPos = mc.player.position();

        for (var entry : HIGHLIGHTS.entrySet()) {
            BlockPos pos = entry.getKey();
            HighlightEntry data = entry.getValue();
            if (!isWithinRenderDistance(pos, playerPos, renderDistanceSq)) return;

            float a = ((data.color >> 24) & 0xFF) / 255.0f;
            float r = ((data.color >> 16) & 0xFF) / 255.0f;
            float g = ((data.color >> 8) & 0xFF) / 255.0f;
            float b = (data.color & 0xFF) / 255.0f;

            //#if MC < 12005
            //$$ PoseStack poseStack = context.matrixStack();
            //$$ poseStack.pushPose();
            //$$ poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
            //$$ Matrix4f matrix = poseStack.last().pose();
            //$$ bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            //$$ renderFilledCube(matrix, bufferBuilder, r, g, b, a);
            //$$ BufferUploader.drawWithShader(bufferBuilder.end());
            //$$ poseStack.popPose();
            //#else
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            renderFilledCube(bufferBuilder, pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z, r, g, b, a);
            BufferUploader.drawWithShader(bufferBuilder.end());
            //#endif
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    private static boolean isWithinRenderDistance(BlockPos pos, Vec3 playerPos, double renderDistanceSq) {
        double dx = pos.getX() + 0.5 - playerPos.x;
        double dy = pos.getY() + 0.5 - playerPos.y;
        double dz = pos.getZ() + 0.5 - playerPos.z;
        double distanceSq = dx * dx + dy * dy + dz * dz;
        return !(distanceSq > renderDistanceSq);
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

    private record HighlightEntry(int color, long expireTime) {}
}