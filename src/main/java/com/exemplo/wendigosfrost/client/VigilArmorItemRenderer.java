package com.exemplo.wendigosfrost.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Axis;

/**
 * Forge BEWLR uses the SAME VigilArmorMesh that the player wears.
 * The builtin/entity item JSON only enables this renderer; it does NOT
 * replace the original BB geometry with a second approximate cube model.
 */
public final class VigilArmorItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static VigilArmorItemRenderer instance;

    private VigilArmorItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
              Minecraft.getInstance().getEntityModels());
    }

    public static VigilArmorItemRenderer instance() {
        if(instance==null)instance=new VigilArmorItemRenderer();
        return instance;
    }

    @Override
    public void renderByItem(ItemStack stack,ItemDisplayContext context,
                             PoseStack pose,MultiBufferSource buffers,
                             int light,int overlay) {
        if(!(stack.getItem() instanceof ArmorItem armor))return;
        VigilArmorMesh mesh=VigilArmorMesh.of(armor.getType());
        pose.pushPose();
        pose.translate(.5f,.5f,.5f);
        switch(context){
            case GUI -> {
                pose.mulPose(Axis.YP.rotationDegrees(28f));
                pose.mulPose(Axis.XP.rotationDegrees(-18f));
            }
            case GROUND -> {
                pose.mulPose(Axis.YP.rotationDegrees(18f));
                pose.mulPose(Axis.XP.rotationDegrees(-7f));
            }
            case FIXED -> pose.mulPose(Axis.YP.rotationDegrees(22f));
            default -> {
                pose.mulPose(Axis.YP.rotationDegrees(12f));
                pose.mulPose(Axis.XP.rotationDegrees(5f));
            }
        }
        float scale=mesh.itemScale();
        pose.scale(scale,scale,scale);
        mesh.drawInventory(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(mesh.texture)),
            light,overlay);
        pose.popPose();
    }
}
