package com.exemplo.wendigosfrost.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

/**
 * Phase additions to the ORIGINAL GLB: no original triangle is deleted or
 * remeshed. One additional GPU buffer animates the exposed icy heart in the torso.
 * The original blue eyes and organic back silhouette remain untouched.
 */
public final class WendigoPhaseDetails {
    private static VertexBuffer heart;
    private static boolean loaded;

    private WendigoPhaseDetails() {}

    private static void tri(BufferBuilder b, float[] a, float[] c, float[] d, int color) {
        vertex(b,a,color);
        vertex(b,c,color);
        vertex(b,d,color);
    }
    private static void vertex(BufferBuilder b,float[] p,int rgba) {
        b.vertex(p[0],p[1],p[2]).color(
            (rgba>>16)&255,(rgba>>8)&255,rgba&255,255).endVertex();
    }
    private static float[] p(float x,float y,float z) {return new float[]{x,y,z};}

    private static void ellipsoid(BufferBuilder b,float cx,float cy,float cz,
                                  float rx,float ry,float rz,int color) {
        final int latitude=9, longitude=14;
        for(int i=0;i<latitude;i++) {
            double ta=Math.PI*i/latitude,tb=Math.PI*(i+1)/latitude;
            for(int j=0;j<longitude;j++) {
                double aa=2*Math.PI*j/longitude,ab=2*Math.PI*(j+1)/longitude;
                float[] a=point(cx,cy,cz,rx,ry,rz,ta,aa);
                float[] b1=point(cx,cy,cz,rx,ry,rz,tb,aa);
                float[] c=point(cx,cy,cz,rx,ry,rz,tb,ab);
                float[] d=point(cx,cy,cz,rx,ry,rz,ta,ab);
                tri(b,a,b1,c,color);tri(b,a,c,d,color);
            }
        }
    }
    private static float[] point(float x,float y,float z,float rx,float ry,float rz,double theta,double phi) {
        return p(x+(float)(rx*Math.sin(theta)*Math.cos(phi)),
                 y+(float)(ry*Math.sin(theta)*Math.sin(phi)),
                 z+(float)(rz*Math.cos(theta)));
    }
    private static void iceSpike(BufferBuilder b,float x,float y,float z,
                                 float sx,float sy,float height,float width) {
        float[] tip=p(x+sx,y+sy,z+height);
        float[][] base={p(x-width,y-width,z),p(x+width,y-width,z),
                        p(x+width,y+width,z),p(x-width,y+width,z)};
        for(int i=0;i<4;i++)
            tri(b,base[i],base[(i+1)%4],tip,i%2==0?0x9EEBFF:0x2D89C9);
        tri(b,base[0],base[2],base[1],0x4480BA);
        tri(b,base[0],base[3],base[2],0x4480BA);
    }
    private static VertexBuffer finish(BufferBuilder builder) {
        VertexBuffer buf=new VertexBuffer(VertexBuffer.Usage.STATIC);
        buf.bind();
        buf.upload(builder.end());
        VertexBuffer.unbind();
        return buf;
    }
    private static void initialize() {
        if (loaded) return;
        RenderSystem.assertOnRenderThread();
        loaded=true;
        // Exposed heart: purple-blue musculature, fractured crystalline core,
        // two anatomical lobes, deep-red vein and six projecting icicles.
        BufferBuilder b=new BufferBuilder(65536);
        b.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        ellipsoid(b,1.83f,-.23f,3.33f,.44f,.33f,.46f,0x71375B);
        ellipsoid(b,1.83f, .23f,3.33f,.44f,.33f,.46f,0x8D385D);
        ellipsoid(b,1.99f,0,3.13f,.36f,.43f,.61f,0x4B3673);
        ellipsoid(b,2.26f,0,3.29f,.23f,.33f,.38f,0x45D9ED);
        ellipsoid(b,2.38f,0,3.30f,.095f,.20f,.24f,0xE0FFFF);
        for(int i=0;i<6;i++) {
            float a=(float)(i*Math.PI/3);
            iceSpike(b,2.17f,(float)Math.sin(a)*.36f,
                     3.25f+(float)Math.cos(a)*.31f,
                     .17f,.10f,.44f,.085f);
        }
        heart=finish(b);
    }
    private static void draw(VertexBuffer buffer,PoseStack pose) {
        if(buffer==null || buffer.isInvalid())return;
        buffer.bind();
        buffer.drawWithShader(pose.last().pose(),RenderSystem.getProjectionMatrix(),
                              RenderSystem.getShader());
    }
    public static void drawHeart(PoseStack pose,float phaseBlend,float tick) {
        initialize();
        if(phaseBlend<=0)return;
        pose.pushPose();
        // Original GLB is a quadruped with its sternum on local -Z.
        // In phase 2, the torso rotates -86 degrees around the hips:
        // -Z becomes FRONT, while +X becomes UP. Older versions placed
        // the heart at x=1.92,z=3.28 (near the throat/back, hidden).
        // Move its center onto the underside of the mid-ribcage, which
        // is the visible upper CHEST after the creature rears upright.
        pose.translate(-1.24f, 0f, -1.20f);
        pose.translate(1.92f,0,3.28f);
        float pulse=1f+.065f*(float)Math.sin(tick*.18f);
        float s=Math.max(.01f,phaseBlend)*pulse;
        pose.scale(s,s,s);
        pose.translate(-1.92f,0,-3.28f);
        draw(heart,pose);
        pose.popPose();
    }
}
