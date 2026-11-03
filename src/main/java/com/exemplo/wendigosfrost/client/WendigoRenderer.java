package com.exemplo.wendigosfrost.client;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.entity.WendigoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import me.danialisntcool.gltfapi.api.client.GltfApi;
import me.danialisntcool.gltfapi.api.client.GltfModelHandle;
import me.danialisntcool.gltfapi.api.client.GltfRenderContext;

/**
 * Draws the ORIGINAL unmodified 70,020-triangle GLB rather than the
 * approximate cuboid GeckoLib model.
 *
 * This asset is static (no skeletal rig). Positioning, bobbing and whole-body
 * posing are possible, but true individually animated limbs require rigging
 * the original Blender mesh in a future update.
 */
public final class WendigoRenderer extends EntityRenderer<WendigoEntity> {
    private static final ResourceLocation ORIGINAL_GLB =
        new ResourceLocation(WendigosFrost.MOD_ID, "models/gltf/wendigo_glacial_exact.glb");
    private static final GltfModelHandle ORIGINAL_MESH = GltfApi.model(ORIGINAL_GLB);
    private static final ResourceLocation UNUSED_FALLBACK_TEXTURE =
        new ResourceLocation(WendigosFrost.MOD_ID, "textures/entity/wendigo.png");

    public WendigoRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.35f;
    }

    @Override
    public void render(WendigoEntity entity, float entityYaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();

        // Minecraft coordinates: Y is up. The original sculpt was made Z-up,
        // so rotate it into the game without modifying a single source vertex.
        pose.mulPose(Axis.YP.rotationDegrees(180f - entityYaw));

        float time = entity.tickCount + partialTick;
        float bob = (float)Math.sin(time * .115f) *
            (entity.getDeltaMovement().horizontalDistanceSqr() > .002 ? .045f : .012f);

        // The physical scale is chosen to match the first-phase quadruped
        // collision box, while preserving the original mesh geometry.
        boolean fourLegged = entity.getBossPhase() == 1;
        float scale = fourLegged ? .417f : .54f;
        pose.translate(-.12f, bob, 0);
        pose.scale(scale, scale, scale);

        // Standing posture is a whole-body lean; the static sculpt cannot
        // genuinely bend its individual knees/forelegs without skinning.
        if (!fourLegged) {
            pose.mulPose(Axis.YP.rotationDegrees(0));
            pose.mulPose(Axis.XP.rotationDegrees(16f));
        }
        pose.mulPose(Axis.XP.rotationDegrees(-90f));

        GltfApi.render(ORIGINAL_MESH, GltfRenderContext.create(pose, buffers, light));
        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(WendigoEntity entity) {
        return UNUSED_FALLBACK_TEXTURE;
    }
}
