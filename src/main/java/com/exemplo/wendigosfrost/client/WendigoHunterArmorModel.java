package com.exemplo.wendigosfrost.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

/**
 * Detailed worn armor geometry, NOT a flat reskin. Over 140 sculptural pieces:
 * deer skull mask, forked antlers, bone ribs, winter runes, cracked ice armor,
 * curved spine fins, claws and reinforced greaves.
 *
 * Geometry attached directly to HumanoidModel parts follows the player's
 * arm/leg/head animations and works with standard Forge armor rendering.
 */
public final class WendigoHunterArmorModel extends HumanoidModel<LivingEntity> {
    private static int seq = 0;
    private WendigoHunterArmorModel(ModelPart root) { super(root); }

    private static void part(PartDefinition parent, String prefix,
                             float x,float y,float z,float dx,float dy,float dz,
                             int u,int v) {
        parent.addOrReplaceChild(prefix + (seq++),
            CubeListBuilder.create().texOffs(u,v).addBox(x,y,z,dx,dy,dz),
            PartPose.ZERO);
    }

    public static WendigoHunterArmorModel create() {
        seq=0;
        MeshDefinition mesh=HumanoidModel.createMesh(new CubeDeformation(.63f),0.0f);
        PartDefinition root=mesh.getRoot();
        PartDefinition head=root.getChild("head");
        PartDefinition body=root.getChild("body");
        PartDefinition la=root.getChild("left_arm"), ra=root.getChild("right_arm");
        PartDefinition ll=root.getChild("left_leg"), rl=root.getChild("right_leg");

        // Deer-skull helmet: ivory forehead, cheek plates, muzzle and jaw.
        part(head,"skull",-4.5f,-8.9f,-4.9f,9,.95f,6.8f,0,64);
        part(head,"skull",-3.4f,-7.9f,-5.4f,6.8f,1.8f,1.0f,0,64);
        part(head,"skull",-2.7f,-6.2f,-5.7f,5.4f,2.3f,.85f,0,64);
        part(head,"skull",-2.1f,-4.0f,-5.9f,4.2f,2.4f,1.15f,0,64);
        part(head,"skull",-1.6f,-1.8f,-6.0f,3.2f,1.1f,1.4f,0,64);
        part(head,"skull",-3.65f,-7.1f,-5.25f,.9f,5.6f,.8f,0,64);
        part(head,"skull",2.75f,-7.1f,-5.25f,.9f,5.6f,.8f,0,64);
        part(head,"fang",-2.8f,-2.4f,-5.7f,.55f,2.5f,.6f,96,64);
        part(head,"fang",2.2f,-2.4f,-5.7f,.55f,2.5f,.6f,96,64);
        for(int i=0;i<5;i++) {
            part(head,"tooth",-1.8f+i*.82f,-.4f,-5.55f,.34f,1.15f,.40f,96,64);
        }
        // Sunken, cold blue eyes against the dark bone mask.
        part(head,"eye",-3.25f,-5.6f,-5.52f,1.45f,.8f,.35f,64,0);
        part(head,"eye",1.80f,-5.6f,-5.52f,1.45f,.8f,.35f,64,0);
        part(head,"rune",-2.4f,-8.4f,-5.22f,4.8f,.45f,.42f,96,16);

        // Horns spread outward and upward: four main stalks and 6 lateral
        // branches on each side, tapered by length. The small segments create
        // a faceted silhouette rather than a uniformly thick tube.
        for(int side : new int[]{-1,1}) {
            for(int i=0;i<15;i++){
                float t=i/14f;
                float x=side*(2.7f+3.4f*t);
                float y=-9.3f-i*.86f;
                float z=-.65f+((i%4)-2)*.28f;
                float w=.95f-t*.48f;
                part(head,"antler",x-w/2,y,z-w/2,w,.97f,w,0,64);
                if(i%3==0)
                    part(head,"frost",x-.18f,y-.45f,z-.20f,.36f,.55f,.40f,64,0);
            }
            for(int fork=0;fork<6;fork++) {
                float bx=side*(3.4f+fork*.55f);
                float by=-11.3f-fork*1.42f;
                for(int j=0;j<5;j++){
                    float t=j/4f;
                    float x=bx+side*t*(1.15f+fork*.24f);
                    float y=by-t*(2f+(fork%2)*.7f);
                    part(head,"fork",x-.30f,y,-.75f+t*.5f,.60f,.62f,.6f,
                        j>=3?96:0,64);
                }
            }
        }

        // Chest plates and ribs. All are external to the base torso, and
        // move naturally with the player.
        part(body,"chest",-4.8f,.0f,-3.2f,9.6f,3.9f,1.65f,32,0);
        part(body,"belly",-3.35f,4.2f,-2.9f,6.7f,6.3f,.85f,0,0);
        part(body,"spine",-.72f,.2f,-3.6f,1.44f,11.5f,.75f,96,16);
        for(int i=0;i<6;i++) {
            float y=2.5f+i*1.55f;
            float w=3.7f-i*.34f;
            part(body,"rib",-w-.1f,y,-3.35f,w, .52f,1f,0,64);
            part(body,"rib",.1f,y,-3.35f,w,.52f,1f,0,64);
        }
        // Strong heart-like ice glyph on the breastplate.
        part(body,"heart",-.95f,3.2f,-3.95f,1.9f,2.9f,.69f,64,0);
        part(body,"heart",-.35f,3.8f,-4.2f,.7f,1.5f,.4f,96,16);
        for(int side : new int[]{-1,1}) {
            float x=side>0?4.1f:-6.2f;
            part(body,"shoulder",x,.0f,-3.3f,2.1f,2.7f,5.6f,32,0);
            for(int i=0;i<5;i++)
                part(body,"spike",x+side*(i%2)*.3f,-.7f-i*.46f,
                     -.6f+i*.65f,.66f,1.15f,.75f,64,0);
        }
        for(int i=0;i<11;i++) {
            float y=i*.95f;
            part(body,"back",-.65f,y,2.4f+i%3*.15f,1.3f,1.35f,
                    1.2f+i%3*.3f,64,0);
        }

        // Carapace arms: long dark shoulder plates, bone ridges and frost claws.
        for(int side : new int[]{-1,1}){
            PartDefinition arm=side>0?la:ra;
            part(arm,"arm",-3.45f,-2.5f,-2.9f,4.25f,5.2f,5.65f,32,0);
            part(arm,"arm",-2.5f,2.9f,-2.45f,3.1f,6.0f,4.9f,0,0);
            for(int i=0;i<7;i++){
                float y=-1.7f+i*1.42f;
                part(arm,"bone",-3.25f,y,-2.7f,1.0f,.66f,5.2f,0,64);
                part(arm,"ice",-.9f,y,-2.8f,.44f,.75f,.74f,64,0);
            }
            for(int i=0;i<4;i++){
                part(arm,"claw",-2.7f+i*.72f,9.3f,-3.3f,.38f,2.4f,
                     .74f,96,64);
            }
        }

        // Reverse-joint effect on shin guards; boots feature long claw tips.
        for(int side : new int[]{-1,1}){
            PartDefinition leg=side>0?ll:rl;
            part(leg,"thigh",-2.35f,-.35f,-2.85f,4.7f,6.6f,5.3f,32,0);
            part(leg,"shin",-2.1f,5.6f,-3.10f,4.2f,5.6f,5.4f,0,0);
            part(leg,"knee",-2.5f,5.4f,-3.5f,5.0f,2.2f,.95f,64,0);
            for(int i=0;i<7;i++) {
                float y=.8f+i*1.36f;
                part(leg,"ridg",-2.3f,y,-3.4f,.62f,.72f,1f,0,64);
                part(leg,"ridg",1.72f,y,-3.4f,.62f,.72f,1f,0,64);
            }
            for(int i=0;i<3;i++){
                part(leg,"talon",-1.62f+i*1.26f,10.6f,-4.25f,.68f,2.55f,
                     1.32f,96,64);
            }
        }

        LayerDefinition layer=LayerDefinition.create(mesh,128,128);
        return new WendigoHunterArmorModel(layer.bakeRoot());
    }
}
