package com.exemplo.wendigosfrost.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Single mesh source for both the inventory item and worn player armor.
 * The atlas and cubes are generated directly from the four user-supplied
 * Blockbench .bbmodel files by scripts/vigilia-bbmodel-import.gradle.
 *
 * Unlike Minecraft CubeListBuilder, face UVs, per-face material selections,
 * nonstandard 15/20 degree cube rotations and the original surface textures
 * are preserved. Only body-part positioning differs between the views.
 */
public final class VigilArmorMesh {
    public enum Bone { HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG }

    private static final EnumMap<ArmorItem.Type, VigilArmorMesh> CACHE =
        new EnumMap<>(ArmorItem.Type.class);

    private static final String[] FACE_NAMES = {
        "north","south","east","west","up","down"
    };

    public final ResourceLocation texture;
    private final List<Face> faces = new ArrayList<>();
    private final float[] itemCenter = new float[3];
    private float itemScale;

    private record Face(Bone bone, float[][] item, float[][] fitted,
                        float[][] uv, float[] itemNormal, float[] fittedNormal) {}

    private VigilArmorMesh(ArmorItem.Type type) {
        String id = id(type);
        this.texture = new ResourceLocation("wendigosfrost",
            "textures/models/armor/vigilia_bb_" + id + ".png");
        ResourceLocation meshFile = new ResourceLocation("wendigosfrost",
            "armor_bb/" + id + ".json");
        JsonObject mesh;
        try (var stream = Minecraft.getInstance().getResourceManager().open(meshFile);
             var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            mesh = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not load supplied Blockbench armor mesh " + meshFile, ex);
        }
        if (mesh.get("textureWidth").getAsInt() != 512 ||
            mesh.get("textureHeight").getAsInt() != 64)
            throw new IllegalStateException("Unexpected armor atlas dimensions: " + meshFile);

        float[] min = {Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY};
        float[] max = {Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY};
        for (JsonElement element : mesh.getAsJsonArray("elements")) {
            JsonObject cube = element.getAsJsonObject();
            String name = cube.get("name").getAsString();
            float[] from = vector(cube.getAsJsonArray("from"));
            float[] to = vector(cube.getAsJsonArray("to"));
            float[] origin = vector(cube.getAsJsonArray("origin"));
            float[] rotation = vector(cube.getAsJsonArray("rotation"));
            float centerX = (from[0]+to[0])*0.5f;
            Bone bone = bone(type,name,centerX);
            JsonObject materials = cube.getAsJsonObject("faces");
            for (int side=0;side<FACE_NAMES.length;side++) {
                JsonObject face = materials.getAsJsonObject(FACE_NAMES[side]);
                if (face == null || face.get("texture").isJsonNull()) continue;
                int material = face.get("texture").getAsInt();
                if (material<0 || material>=8)
                    throw new IllegalStateException("Invalid original Blockbench material "+material);
                float[] sourceUV = vector(face.getAsJsonArray("uv"));
                float[][] item = makeFace(from,to,side);
                float[][] fitted = new float[4][3];
                float[][] texcoords = new float[4][2];
                for(int i=0;i<4;i++) {
                    rotate(item[i],origin,rotation);
                    for(int k=0;k<3;k++){
                        min[k] = Math.min(min[k],item[i][k]);
                        max[k] = Math.max(max[k],item[i][k]);
                    }
                    fitted[i] = fitToPlayer(type,bone,item[i]);
                    // Blockbench face UV uses the original 64x64 material.
                    // Each original material is pasted at x=64*index in
                    // the combined 512x64 atlas, retaining every source pixel.
                    float u = (i==0 || i==3) ? sourceUV[0] : sourceUV[2];
                    float v = (i==0 || i==1) ? sourceUV[1] : sourceUV[3];
                    texcoords[i][0] = (material*64f + Math.max(0,Math.min(64,u)))/512f;
                    texcoords[i][1] = Math.max(0,Math.min(64,v))/64f;
                }
                faces.add(new Face(bone,item,fitted,texcoords,
                    normal(item),normal(fitted)));
            }
        }
        if (faces.isEmpty()) throw new IllegalStateException("Empty user-supplied mesh "+meshFile);
        float largest=0;
        for(int k=0;k<3;k++){
            itemCenter[k]=(min[k]+max[k])*.5f;
            largest=Math.max(largest,max[k]-min[k]);
        }
        // Visual fill in inventory without clipping the antlers / broad chest.
        itemScale=Math.min(1.20f,13.8f / largest);
    }

    public static VigilArmorMesh of(ArmorItem.Type type) {
        synchronized(CACHE) {
            return CACHE.computeIfAbsent(type,VigilArmorMesh::new);
        }
    }
    private static String id(ArmorItem.Type type) {
        return switch(type) {
            case HELMET -> "elmo";
            case CHESTPLATE -> "peitoral";
            case LEGGINGS -> "calcas";
            case BOOTS -> "botas";
        };
    }

    private static float[] vector(JsonArray v) {
        float[] arr=new float[v.size()];
        for(int i=0;i<arr.length;i++)arr[i]=v.get(i).getAsFloat();
        return arr;
    }

    private static Bone bone(ArmorItem.Type type,String name,float cx) {
        if(type==ArmorItem.Type.HELMET) return Bone.HEAD;
        if(type==ArmorItem.Type.CHESTPLATE) {
            if(name.contains("shoulder"))
                return cx<0?Bone.RIGHT_ARM:Bone.LEFT_ARM;
            return Bone.BODY;
        }
        if(type==ArmorItem.Type.LEGGINGS && name.contains("waist"))
            return Bone.BODY;
        return cx<0?Bone.RIGHT_LEG:Bone.LEFT_LEG;
    }

    /**
     * Mapping from the supplied free-Blockbench coordinates (up=+Y)
     * to the matching vanilla player bone (down=+Y).
     *
     * The helmet follows the head; the chest shoulders follow the ARMS;
     * the pants and boots follow EACH leg rather than staying in world space.
     * The small leg scale makes the wider 7px designer greaves fit a 4px
     * Minecraft player leg while preserving proportions and all UV pixels.
     */
    private static float[] fitToPlayer(ArmorItem.Type type,Bone bone,float[] p) {
        float x=p[0], y=p[1],z=p[2];
        if(type==ArmorItem.Type.HELMET)
            return new float[]{x*1.075f, -y*1.075f, z*1.075f};
        if(type==ArmorItem.Type.CHESTPLATE){
            if(bone==Bone.RIGHT_ARM || bone==Bone.LEFT_ARM){
                float pivot=(bone==Bone.RIGHT_ARM?-5f:5f);
                return new float[]{(x-pivot)*.96f,10f-y,z*1.02f};
            }
            return new float[]{x*.99f,12f-y,z*1.08f};
        }
        if(bone==Bone.BODY) // Waist of the leggings.
            return new float[]{x*.70f,12f-y,z*.85f};
        float center=(bone==Bone.RIGHT_LEG?-3.5f:3.5f);
        return new float[]{(x-center)*.73f,12f-y,z*.79f};
    }

    // Original Blockbench arbitrary angle in X/Y/Z, including 15 and 20 degrees.
    private static void rotate(float[] pos,float[] origin,float[] angles){
        if(angles[0]==0 && angles[1]==0 && angles[2]==0)return;
        Vector3f v=new Vector3f(pos[0]-origin[0],pos[1]-origin[1],pos[2]-origin[2]);
        new Quaternionf().rotationXYZ((float)Math.toRadians(angles[0]),
            (float)Math.toRadians(angles[1]),(float)Math.toRadians(angles[2])).transform(v);
        pos[0]=v.x+origin[0];pos[1]=v.y+origin[1];pos[2]=v.z+origin[2];
    }

    private static float[][] makeFace(float[] a,float[] b,int index) {
        float x0=a[0],y0=a[1],z0=a[2],x1=b[0],y1=b[1],z1=b[2];
        return switch(index) {
            case 0 -> new float[][]{{x0,y1,z0},{x0,y0,z0},{x1,y0,z0},{x1,y1,z0}}; // north
            case 1 -> new float[][]{{x1,y1,z0},{x1,y0,z0},{x1,y0,z1},{x1,y1,z1}}; // east
            case 2 -> new float[][]{{x1,y1,z1},{x1,y0,z1},{x0,y0,z1},{x0,y1,z1}}; // south
            case 3 -> new float[][]{{x0,y1,z1},{x0,y0,z1},{x0,y0,z0},{x0,y1,z0}}; // west
            case 4 -> new float[][]{{x0,y1,z1},{x0,y1,z0},{x1,y1,z0},{x1,y1,z1}}; // up
            default -> new float[][]{{x0,y0,z0},{x0,y0,z1},{x1,y0,z1},{x1,y0,z0}}; // down
        };
    }

    private static float[] normal(float[][] p){
        float ax=p[1][0]-p[0][0], ay=p[1][1]-p[0][1], az=p[1][2]-p[0][2];
        float bx=p[2][0]-p[0][0], by=p[2][1]-p[0][1], bz=p[2][2]-p[0][2];
        float nx=ay*bz-az*by, ny=az*bx-ax*bz, nz=ax*by-ay*bx;
        float len=(float)Math.sqrt(nx*nx+ny*ny+nz*nz);
        return len<.00001f?new float[]{0,1,0}:new float[]{nx/len,ny/len,nz/len};
    }

    public void drawBone(Bone bone,PoseStack pose,VertexConsumer buffer,
                         int light,int overlay,float r,float g,float b,float a) {
        for(Face face:faces) if(face.bone()==bone)
            draw(face,face.fitted(),face.fittedNormal(),pose,buffer,light,overlay,r,g,b,a);
    }

    public void drawInventory(PoseStack pose,VertexConsumer buffer,int light,int overlay) {
        pose.pushPose();
        pose.translate(-itemCenter[0]/16,-itemCenter[1]/16,-itemCenter[2]/16);
        for(Face face:faces)
            draw(face,face.item(),face.itemNormal(),pose,buffer,light,overlay,1f,1f,1f,1f);
        pose.popPose();
    }
    public float itemScale(){return itemScale;}

    private static void draw(Face face,float[][] points,float[] n,PoseStack pose,
                             VertexConsumer buffer,int light,int overlay,
                             float r,float g,float b,float a) {
        var entry=pose.last();
        for(int i=0;i<4;i++){
            buffer.vertex(entry.pose(),points[i][0]/16,points[i][1]/16,points[i][2]/16)
                .color(r,g,b,a)
                .uv(face.uv()[i][0],face.uv()[i][1])
                .overlayCoords(overlay).uv2(light)
                .normal(entry.normal(),n[0],n[1],n[2]).endVertex();
        }
    }
}
