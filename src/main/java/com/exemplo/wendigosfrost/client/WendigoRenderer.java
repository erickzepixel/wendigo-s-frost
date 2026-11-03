package com.exemplo.wendigosfrost.client;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.entity.WendigoEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Native OpenGL VBO renderer using the *exact* original 70,020 GLB triangles.
 * GPU upload happens once, not once per tick. No new client-side dependency.
 * This model was delivered without a skeleton, so only rigid body movement
 * is available until the original mesh is rigged and weight-painted.
 */
public final class WendigoRenderer extends EntityRenderer<WendigoEntity> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation STREAM =
        new ResourceLocation(WendigosFrost.MOD_ID, "models/mesh/wendigo_glacial.wfmesh");
    private static final ResourceLocation LEGACY_TEXTURE =
        new ResourceLocation(WendigosFrost.MOD_ID, "textures/entity/wendigo.png");
    private static VertexBuffer gpuMesh;
    private static boolean attempted;

    public WendigoRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 1.35f;
    }

    private static int littleInt(DataInputStream input) throws IOException {
        return Integer.reverseBytes(input.readInt());
    }
    private static float littleFloat(DataInputStream input) throws IOException {
        return Float.intBitsToFloat(littleInt(input));
    }

    private static void initializeMesh() {
        if (attempted) return;
        attempted = true;
        RenderSystem.assertOnRenderThread();
        try (InputStream stream = Minecraft.getInstance().getResourceManager().open(STREAM);
             DataInputStream input = new DataInputStream(stream)) {
            byte[] magic = input.readNBytes(4);
            if (magic.length != 4 || magic[0] != 'W' || magic[1] != 'F' ||
                magic[2] != 'M' || magic[3] != 'S') throw new IOException("Invalid mesh header");
            int triangles = littleInt(input);
            if (triangles != 70020) throw new IOException("Unexpected triangle count: " + triangles);

            BufferBuilder builder = new BufferBuilder(8 * 1024 * 1024);
            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
            for (int t = 0; t < triangles; t++) {
                int r = input.readUnsignedByte(), g = input.readUnsignedByte();
                int b = input.readUnsignedByte(), alpha = input.readUnsignedByte();
                // Source triangle normal is stored for future lit renderer upgrades.
                littleFloat(input); littleFloat(input); littleFloat(input);
                for (int vertex = 0; vertex < 3; vertex++) {
                    float x = littleFloat(input), y = littleFloat(input), z = littleFloat(input);
                    builder.vertex(x, y, z).color(r, g, b, alpha).endVertex();
                }
            }
            gpuMesh = new VertexBuffer(VertexBuffer.Usage.STATIC);
            gpuMesh.bind();
            gpuMesh.upload(builder.end());
            VertexBuffer.unbind();
            LOGGER.info("Wendigo's Frost: original 70,020-triangle GPU mesh uploaded");
        } catch (Exception failure) {
            VertexBuffer.unbind();
            LOGGER.error("Wendigo's Frost: original mesh could not be rendered", failure);
        }
    }

    @Override
    public void render(WendigoEntity boss, float yaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffers, int packedLight) {
        initializeMesh();
        if (gpuMesh == null || gpuMesh.isInvalid()) return;
        pose.pushPose();
        // Original asset is Z-up, glTF exported without rig or axis correction.
        // 90 degrees of yaw maps the model's original +X (muzzle) to forward -Z.
        pose.mulPose(Axis.YP.rotationDegrees(270f - yaw));
        float time = boss.tickCount + partialTick;
        boolean quad = boss.getBossPhase() == 1;
        float motion = (float)Math.min(1, boss.getDeltaMovement().horizontalDistanceSqr() * 25);
        float bob = (float)Math.sin(time * (quad ? .13 : .08)) * motion * .035f;
        pose.translate(-.1f, bob, 0f);
        float size = quad ? .417f : .61f;
        pose.scale(size, size, size);
        // Rigid pose change only; individual limb animation requires skinning.
        if (!quad) pose.mulPose(Axis.XP.rotationDegrees(12f));
        pose.mulPose(Axis.XP.rotationDegrees(-90f));

        // Position-color material renderer; the GLB's original material factors
        // are baked per triangle, preserving all of the original source geometry.
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        gpuMesh.bind();
        gpuMesh.drawWithShader(pose.last().pose(), RenderSystem.getProjectionMatrix(),
            RenderSystem.getShader());
        VertexBuffer.unbind();
        RenderSystem.enableCull();

        pose.popPose();
        super.render(boss, yaw, partialTick, pose, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WendigoEntity boss) {
        return LEGACY_TEXTURE;
    }
}
