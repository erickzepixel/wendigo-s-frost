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
import net.minecraft.util.Mth;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Articulated rigid-part skeleton driven by the actual original GLB surface:
 * 70,020 triangles unchanged; each connected surface receives a bone.
 * Each of ten bones gets a GPU-cached VertexBuffer, not an upload per frame.
 *
 * The model was not originally skinned. Parts rotate at anatomical joints;
 * no interpolation of vertices across joints (possible minor seams).
 * Original GLB is preserved byte-identically in the resource pack.
 */
public final class WendigoRenderer extends EntityRenderer<WendigoEntity> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation RIGGED_STREAM =
        new ResourceLocation(WendigosFrost.MOD_ID, "models/mesh/wendigo_articulado.wfar");
    private static final ResourceLocation FALLBACK_TEXTURE =
        new ResourceLocation(WendigosFrost.MOD_ID, "textures/entity/wendigo.png");
    // 0 torso, 1 head, 2/3 front left upper/lower, 4/5 front right,
    // 6/7 hind left upper/lower, 8/9 hind right.
    private static final int BONES = 10;
    private static final VertexBuffer[] GPU_BONES = new VertexBuffer[BONES];
    private static boolean attempted;

    public WendigoRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 1.4f;
    }

    private static int intLE(DataInputStream input) throws IOException {
        return Integer.reverseBytes(input.readInt());
    }
    private static float floatLE(DataInputStream input) throws IOException {
        return Float.intBitsToFloat(intLE(input));
    }

    private static void loadBones() {
        if (attempted) return;
        attempted = true;
        RenderSystem.assertOnRenderThread();
        try (InputStream stream = Minecraft.getInstance().getResourceManager().open(RIGGED_STREAM);
             DataInputStream input = new DataInputStream(stream)) {
            byte[] magic = input.readNBytes(4);
            if (magic.length != 4 || magic[0] != 'W' || magic[1] != 'F' ||
                    magic[2] != 'A' || magic[3] != 'R') throw new IOException("Invalid rig header");
            if (intLE(input) != 1 || intLE(input) != BONES)
                throw new IOException("Unsupported rig version or number of bones");

            int allTriangles = 0;
            for (int i = 0; i < BONES; ++i) {
                int id = intLE(input), triangles = intLE(input);
                if (id != i || triangles < 1 || triangles > 70020)
                    throw new IOException("Invalid rig group " + id + " (" + triangles + ")");
                allTriangles += triangles;
                BufferBuilder builder = new BufferBuilder(Math.max(524288, triangles * 84));
                builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
                for (int t = 0; t < triangles; t++) {
                    int r = input.readUnsignedByte(), g = input.readUnsignedByte();
                    int b = input.readUnsignedByte(), alpha = input.readUnsignedByte();
                    float nx = floatLE(input), ny = floatLE(input), nz = floatLE(input);
                    for (int v = 0; v < 3; ++v) {
                        float x = floatLE(input), y = floatLE(input), z = floatLE(input);
                        builder.vertex(x, y, z).color(r, g, b, alpha).endVertex();
                    }
                }
                GPU_BONES[i] = new VertexBuffer(VertexBuffer.Usage.STATIC);
                GPU_BONES[i].bind();
                GPU_BONES[i].upload(builder.end());
                VertexBuffer.unbind();
            }
            if (allTriangles != 70020)
                throw new IOException("Expected 70,020 original triangles, got " + allTriangles);
            LOGGER.info("Wendigo's Frost: 70,020 original triangles in ten animated VBO bones");
        } catch (Exception ex) {
            LOGGER.error("Could not initialize original articulated Wendigo mesh", ex);
            VertexBuffer.unbind();
            for (int i=0; i<BONES; i++) {
                if (GPU_BONES[i] != null) GPU_BONES[i].close();
                GPU_BONES[i] = null;
            }
        }
    }

    private static void drawBone(int bone, PoseStack pose) {
        VertexBuffer buffer = GPU_BONES[bone];
        if (buffer == null || buffer.isInvalid()) return;
        buffer.bind();
        buffer.drawWithShader(pose.last().pose(), RenderSystem.getProjectionMatrix(),
            RenderSystem.getShader());
    }

    // Coordinates are in the original Z-up GLB space (+X is nose).
    private static void hinge(PoseStack pose, float x, float y, float z, float degrees) {
        pose.translate(x, y, z);
        pose.mulPose(Axis.YP.rotationDegrees(degrees));
        pose.translate(-x, -y, -z);
    }

    private static void frontLeg(PoseStack pose, boolean left, float t, float speed,
                                 float locomotion, float stand, float attack,
                                 float roar, float stomp) {
        int upper = left ? 2 : 4, lower = left ? 3 : 5;
        float side = left ? 1f : -1f;
        float gait = (float)Math.sin(t*speed+(left ? 0 : Math.PI));
        float armSwing = gait * (1f-stand) * locomotion * 28f +
            gait * stand * locomotion * 16f;
        // Counter-pitch the erect ribcage so arms HANG vertically, rather
        // than extending sideways. Each strike is a visible 14 tick arc.
        float action = attack * (left ? -75f : -48f) +
                       roar * -38f + stomp * -70f;
        pose.pushPose();
        hinge(pose, 1.07f, side * .91f, 3.4f,
            armSwing + stand * 83f + action);
        drawBone(upper, pose);
        pose.pushPose();
        float elbow = ((float)Math.max(0, Math.sin(t*speed+(left ? .35 : Math.PI+.35))))
            * locomotion * 22f * (1f-stand) + stand * (7f+attack*44f+stomp*24f);
        hinge(pose, 1.52f, side*1.29f, 1.68f, elbow);
        drawBone(lower, pose);
        pose.popPose();
        pose.popPose();
    }

    private static void hindLeg(PoseStack pose, boolean left, float t, float speed,
                                float locomotion, float stand) {
        int upper = left ? 6 : 8, lower = left ? 7 : 9;
        float side=left ? 1f : -1f;
        float gait=(float)Math.sin(t*speed+(left ? Math.PI : 0));
        pose.pushPose();
        hinge(pose, -2.19f, side*.86f, 2.78f, gait*locomotion*(21f-stand*12f));
        drawBone(upper,pose);
        pose.pushPose();
        float knee=(float)Math.max(0, Math.sin(t*speed+(left ? Math.PI : 0)));
        hinge(pose, -2.42f, side*.98f, 1.40f,
            knee*locomotion*(22f-stand*14f));
        drawBone(lower,pose);
        pose.popPose();
        pose.popPose();
    }

    @Override
    public void render(WendigoEntity entity, float yaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffers, int packedLight) {
        loadBones();
        if (GPU_BONES[0] == null) {
            super.render(entity, yaw, partialTick, pose, buffers, packedLight);
            return;
        }

        pose.pushPose();
        float t = entity.tickCount + partialTick;
        float stand = entity.getStandBlend(partialTick);
        float horizontalSpeed=(float)Math.sqrt(entity.getDeltaMovement().horizontalDistanceSqr());
        float locomotion=Math.min(1f,horizontalSpeed*5.5f);
        float speed=entity.getBossPhase()==3 ? .37f : (entity.getBossPhase()==1 ? .25f : .29f);
        float breath=(float)Math.sin(t*.084f)*.018f;
        float walkBob=(float)Math.abs(Math.sin(t*speed))*locomotion*.045f;
        float attack=Math.max(entity.getAttackAnim(partialTick), entity.getSwipeBlend(partialTick));
        float roar=(entity.getAttackCue()==1 || entity.getAttackCue()==3) ? 1f : 0f;
        float stomp=(entity.getAttackCue()==2 || entity.getAttackCue()==4) ? 1f : 0f;

        // Convert original +X muzzle, Z-up coordinates to Minecraft axes.
        pose.mulPose(Axis.YP.rotationDegrees(270f-yaw));
        float scale=.445f;
        pose.scale(scale,scale,scale);
        pose.mulPose(Axis.XP.rotationDegrees(-90f));
        pose.translate(0, 0, walkBob + breath);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        // The static-mesh renderer bypassed Minecraft's usual hurt overlay.
        // Multiply the actual VBO vertex colors by red while hurtTime > 0.
        boolean flashing = entity.hurtTime > 0 || entity.deathTime > 0;
        if (flashing) {
            float flicker = .25f + .12f *
                (float)Math.abs(Math.sin((t)*1.15f));
            RenderSystem.setShaderColor(1f, flicker, flicker, 1f);
        } else {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        // Hind legs keep supporting the creature as its torso rears upward.
        hindLeg(pose,true,t,speed,locomotion,stand);
        hindLeg(pose,false,t,speed,locomotion,stand);

        pose.pushPose();
        // Pivot the ribcage around the hips. Front arms and head inherit this
        // motion; this creates the true quadruped-to-biped upper-body transition.
        // From horizontal animal spine to an erect humanoid thorax (86 degrees).
        // Hind paws stay planted while chest/head/forearms pivot together.
        hinge(pose,-1.85f,0f,2.80f,-86f*stand);
        drawBone(0,pose);
        if(stand > 0f)
            WendigoPhaseDetails.drawHeart(pose,stand,t);

        pose.pushPose();
        float headYaw = Mth.clamp(
            Mth.wrapDegrees(entity.getYHeadRot()-entity.yBodyRot), -42f, 42f);
        // Cancel the torso pitch at the neck: muzzle and glowing eyes look
        // FORWARD at the target rather than toward the sky in phase two.
        pose.translate(2.0f, 0f, 3.19f);
        pose.mulPose(Axis.YP.rotationDegrees(
            86f*stand + (float)Math.sin(t*.07)*2f - roar*24f + stomp*11f));
        pose.mulPose(Axis.ZP.rotationDegrees(headYaw));
        pose.translate(-2.0f, 0f, -3.19f);
        drawBone(1,pose);
        pose.popPose();

        frontLeg(pose,true,t,speed,locomotion,stand,attack,roar,stomp);
        frontLeg(pose,false,t,speed,locomotion,stand,attack,roar,stomp);
        pose.popPose();

        VertexBuffer.unbind();
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.enableCull();
        pose.popPose();
        super.render(entity,yaw,partialTick,pose,buffers,packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WendigoEntity entity) {
        return FALLBACK_TEXTURE;
    }
}
