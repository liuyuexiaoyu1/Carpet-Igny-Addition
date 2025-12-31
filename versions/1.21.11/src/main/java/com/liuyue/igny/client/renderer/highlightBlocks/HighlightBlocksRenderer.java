package com.liuyue.igny.client.renderer.highlightBlocks;

import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.client.renderer.substitute.WorldRenderContext;
import com.liuyue.igny.client.renderer.substitute.WorldRenderEvents;
import com.liuyue.igny.mixins.render.RenderTypeInvoker;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class HighlightBlocksRenderer {



    private static final RenderPipeline HIGHLIGHT_PIPELINE =
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(IGNYServer.MOD_ID + "/highlight_pipeline")
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
                    .build();

    public static final RenderType HIGHLIGHT_BLOCKS = createHighlightRenderType();

    private static final Map<BlockPos, HighlightEntry> HIGHLIGHTS = new HashMap<>();

    private static RenderType createHighlightRenderType() {
        var renderSetup = RenderSetup.builder(HIGHLIGHT_PIPELINE)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .setTextureTransform(TextureTransform.DEFAULT_TEXTURING)
                .setOutline(RenderSetup.OutlineProperty.NONE)
                .affectsCrumbling()
                .sortOnUpload()
                .bufferSize(256)
                .createRenderSetup();

        return RenderTypeInvoker.create("highlight_block", renderSetup);
    }

    public static void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(HighlightBlocksRenderer::onWorldRender);
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
        if (HIGHLIGHTS.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        long now = mc.level.getGameTime();

        HIGHLIGHTS.entrySet().removeIf(blockPosHighlightEntryEntry -> blockPosHighlightEntryEntry.getValue().expireTime <= now);
        if (HIGHLIGHTS.isEmpty()) return;

        PoseStack poseStack = context.poseStack();
        Vec3 cameraPos = context.camera().position();

        if (poseStack != null) {
            BufferBuilder vc = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_COLOR);

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

                Matrix4f localMatrix = new Matrix4f();
                localMatrix.translation((float) dx, (float) dy, (float) dz);
                renderFilledCube(vc, localMatrix, r, g, b, a);
            }
            try{
                GlStateManager._disableDepthTest();
                GlStateManager._enableBlend();

                MeshData meshData = vc.buildOrThrow();
                HIGHLIGHT_BLOCKS.draw(meshData);

                GlStateManager._enableDepthTest();
                GlStateManager._disableBlend();
            }catch (Exception ignored) {}
        }
    }

    private static void renderFilledCube(
            VertexConsumer vc,
            Matrix4f m,
            float r, float g, float b, float a
    ) {
        float min = -0.5f;
        float max = 0.5f;

        quad(vc, m,
                min, max, min,
                max, max, min,
                max, max, max,
                min, max, max,
                r, g, b, a
        );

        quad(vc, m,
                min, min, max,
                max, min, max,
                max, min, min,
                min, min, min,
                r, g, b, a
        );

        quad(vc, m,
                max, min, min,
                max, min, max,
                max, max, max,
                max, max, min,
                r, g, b, a
        );

        quad(vc, m,
                min, min, max,
                min, min, min,
                min, max, min,
                min, max, max,
                r, g, b, a
        );

        quad(vc, m,
                min, min, max,
                max, min, max,
                max, max, max,
                min, max, max,
                r, g, b, a
        );

        quad(vc, m,
                max, min, min,
                min, min, min,
                min, max, min,
                max, max, min,
                r, g, b, a
        );
    }

    private static void quad(
            VertexConsumer vc,
            Matrix4f m,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            float r, float g, float b, float a
    ) {
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(m, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(m, x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(m, x4, y4, z4).setColor(r, g, b, a);
    }

    private record HighlightEntry(int color, long expireTime) {}
}
