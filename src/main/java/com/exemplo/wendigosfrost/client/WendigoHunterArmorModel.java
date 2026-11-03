package com.exemplo.wendigosfrost.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import java.util.List;
import java.util.Map;

/**
 * User-authored Blockbench armor, not the obsolete synthetic deer-skull mesh.
 * The EXACT geometry and PNG pixel UVs come from VigilArmorMesh and are shared
 * with the inventory's BlockEntityWithoutLevelRenderer.
 *
 * The humanoid skeleton has NO vanilla cubes, so the player cannot get a
 * second overlapping dummy armor layer under the uploaded original geometry.
 */
public final class WendigoHunterArmorModel extends HumanoidModel<LivingEntity> {
    private final ArmorItem.Type piece;
    private final VigilArmorMesh mesh;

    private static ModelPart empty() {
        return new ModelPart(List.of(), Map.of());
    }
    private static ModelPart skeleton() {
        ModelPart head=empty(), hat=empty(), body=empty();
        ModelPart leftArm=empty(),rightArm=empty(),leftLeg=empty(),rightLeg=empty();
        head.setPos(0,0,0);
        hat.setPos(0,0,0);
        body.setPos(0,0,0);
        leftArm.setPos(5,2,0);
        rightArm.setPos(-5,2,0);
        leftLeg.setPos(1.9f,12,0);
        rightLeg.setPos(-1.9f,12,0);
        return new ModelPart(List.of(),Map.of(
            "head",head,"hat",hat,"body",body,
            "left_arm",leftArm,"right_arm",rightArm,
            "left_leg",leftLeg,"right_leg",rightLeg
        ));
    }

    private WendigoHunterArmorModel(ArmorItem.Type type){
        super(skeleton());
        this.piece=type;
        this.mesh=VigilArmorMesh.of(type);
        this.hat.visible=false;
    }
    public static WendigoHunterArmorModel create(ArmorItem.Type type){
        return new WendigoHunterArmorModel(type);
    }

    private void drawBone(PoseStack pose,VertexConsumer buffer,
                          int light,int overlay,float r,float g,float b,float a,
                          ModelPart part,VigilArmorMesh.Bone bone) {
        pose.pushPose();
        part.translateAndRotate(pose);
        mesh.drawBone(bone,pose,buffer,light,overlay,r,g,b,a);
        pose.popPose();
    }

    @Override
    public void renderToBuffer(PoseStack pose,VertexConsumer buffer,
                               int light,int overlay,
                               float r,float g,float b,float a) {
        // Forge copies the player head/body/leg/arm poses before this draw:
        // walking, sneaking, sprinting, swimming and crouching move the
        // matching pieces independently, without separating their textures.
        switch(piece) {
            case HELMET -> drawBone(pose,buffer,light,overlay,r,g,b,a,
                head,VigilArmorMesh.Bone.HEAD);
            case CHESTPLATE -> {
                drawBone(pose,buffer,light,overlay,r,g,b,a,
                    body,VigilArmorMesh.Bone.BODY);
                drawBone(pose,buffer,light,overlay,r,g,b,a,
                    leftArm,VigilArmorMesh.Bone.LEFT_ARM);
                drawBone(pose,buffer,light,overlay,r,g,b,a,
                    rightArm,VigilArmorMesh.Bone.RIGHT_ARM);
            }
            case LEGGINGS -> {
                drawBone(pose,buffer,light,overlay,r,g,b,a,
                    body,VigilArmorMesh.Bone.BODY); // uploaded waist belt
                drawBone(pose,buffer,light,overlay,r,g,b,a,
                    leftLeg,VigilArmorMesh.Bone.LEFT_LEG);
                drawBone(pose,buffer,light,overlay,r,g,b,a,
                    rightLeg,VigilArmorMesh.Bone.RIGHT_LEG);
            }
            case BOOTS -> {
                drawBone(pose,buffer,light,overlay,r,g,b,a,
                    leftLeg,VigilArmorMesh.Bone.LEFT_LEG);
                drawBone(pose,buffer,light,overlay,r,g,b,a,
                    rightLeg,VigilArmorMesh.Bone.RIGHT_LEG);
            }
        }
    }
}
