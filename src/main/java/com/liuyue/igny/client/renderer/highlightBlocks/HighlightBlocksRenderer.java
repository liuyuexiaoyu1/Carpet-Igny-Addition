package com.liuyue.igny.client.renderer.highlightBlocks;

//#if MC >= 12105
//$$ import com.mojang.blaze3d.opengl.GlStateManager;
//$$ import net.minecraft.client.renderer.RenderType;
//$$ import net.minecraft.client.renderer.RenderStateShard;
//$$ import org.joml.Matrix4f;
//$$ import com.mojang.blaze3d.pipeline.RenderPipeline;
//$$ import com.mojang.blaze3d.platform.DepthTestFunction;
//$$ import net.minecraft.client.renderer.RenderPipelines;
//$$ import net.minecraft.resources.ResourceLocation;
//$$ import com.liuyue.igny.IGNYServer;
//$$ import java.util.OptionalDouble;
//#else
import com.mojang.blaze3d.platform.GlStateManager;
//#endif

//#if MC > 12101
//#if MC < 12105
//$$ import net.minecraft.client.renderer.CoreShaders;
//#endif
//#endif
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
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class HighlightBlocksRenderer {

    private static final Map<BlockPos, HighlightEntry> HIGHLIGHTS = new HashMap<>();

    //#if MC >= 12105
    //$$ private static final RenderPipeline noDepthQuads = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
    //$$       .withLocation(ResourceLocation.fromNamespaceAndPath(IGNYServer.MOD_ID, "no_depth_quads"))
    //$$       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
    //$$      .withDepthWrite(false)
    //$$      .withCull(false)
    //$$       .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
    //$$      .build();
    //$$ public static final RenderType HIGHLIGHT_BLOCKS =
    //$$ RenderType.create(
    //$$          "highlight_block",
    //$$          256,
    //$$          false,
    //$$          true,
    //$$          noDepthQuads,
    //$$          RenderType.CompositeState.builder()
    //$$                  .setTextureState(RenderStateShard.NO_TEXTURE)
    //$$                  .setLightmapState(RenderStateShard.NO_LIGHTMAP)
    //$$                  .setOverlayState(RenderStateShard.NO_OVERLAY)
    //$$                  .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
    //$$                  .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
    //$$                  .setTexturingState(RenderStateShard.DEFAULT_TEXTURING)
    //$$                  .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
    //$$                  .createCompositeState(false));
    //#endif

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
        if (HIGHLIGHTS.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        long now = mc.level.getGameTime();

        HIGHLIGHTS.entrySet().removeIf(blockPosHighlightEntryEntry -> blockPosHighlightEntryEntry.getValue().expireTime <= now);
        if (HIGHLIGHTS.isEmpty()) return;

        PoseStack poseStack = context.matrixStack();
        Vec3 cameraPos = context.camera().getPosition();

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

                renderFilledCube(vc, poseStack.last().pose(), (float)dx, (float)dy, (float)dz, r, g, b, a);
            }
            try {
                //#if MC <= 12101
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                //#elseif MC < 12105
                //$$ RenderSystem.setShader(CoreShaders.POSITION_COLOR);
                //#endif
                GlStateManager._disableDepthTest();
                GlStateManager._disableCull();
                GlStateManager._enableBlend();

                MeshData meshData = vc.buildOrThrow();

                //#if MC >= 12105
                //$$ HIGHLIGHT_BLOCKS.draw(meshData);
                //#else
                BufferUploader.drawWithShader(meshData);
                //#endif

                //#if MC < 12105
                GlStateManager._enableDepthTest();
                GlStateManager._disableBlend();
                GlStateManager._enableCull();
                //#endif
            } catch (Exception ignored) {}
        }
    }


    private static void renderFilledCube(
            VertexConsumer vc,
            Matrix4f m,
            float centerX, float centerY, float centerZ,
            float r, float g, float b, float a
    ) {
        float min = -0.5f;
        float max = 0.5f;

        quad(vc, m,
                centerX + min, centerY + max, centerZ + min,
                centerX + max, centerY + max, centerZ + min,
                centerX + max, centerY + max, centerZ + max,
                centerX + min, centerY + max, centerZ + max,
                r, g, b, a
        );

        quad(vc, m,
                centerX + min, centerY + min, centerZ + max,
                centerX + max, centerY + min, centerZ + max,
                centerX + max, centerY + min, centerZ + min,
                centerX + min, centerY + min, centerZ + min,
                r, g, b, a
        );

        quad(vc, m,
                centerX + max, centerY + min, centerZ + min,
                centerX + max, centerY + min, centerZ + max,
                centerX + max, centerY + max, centerZ + max,
                centerX + max, centerY + max, centerZ + min,
                r, g, b, a
        );

        quad(vc, m,
                centerX + min, centerY + min, centerZ + max,
                centerX + min, centerY + min, centerZ + min,
                centerX + min, centerY + max, centerZ + min,
                centerX + min, centerY + max, centerZ + max,
                r, g, b, a
        );

        quad(vc, m,
                centerX + min, centerY + min, centerZ + max,
                centerX + max, centerY + min, centerZ + max,
                centerX + max, centerY + max, centerZ + max,
                centerX + min, centerY + max, centerZ + max,
                r, g, b, a
        );

        quad(vc, m,
                centerX + max, centerY + min, centerZ + min,
                centerX + min, centerY + min, centerZ + min,
                centerX + min, centerY + max, centerZ + min,
                centerX + max, centerY + max, centerZ + min,
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
        //#if MC < 12100
        //$$ vc.vertex(m, x1, y1, z1).color(r, g, b, a).endVertex();
        //$$ vc.vertex(m, x2, y2, z2).color(r, g, b, a).endVertex();
        //$$ vc.vertex(m, x3, y3, z3).color(r, g, b, a).endVertex();
        //$$ vc.vertex(m, x4, y4, z4).color(r, g, b, a).endVertex();
        //#else
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(m, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(m, x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(m, x4, y4, z4).setColor(r, g, b, a);
        //#endif
    }

    private record HighlightEntry(int color, long expireTime) {}
}
