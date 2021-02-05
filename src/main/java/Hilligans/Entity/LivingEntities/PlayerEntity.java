package Hilligans.Entity.LivingEntities;

import Hilligans.Client.MatrixStack;
import Hilligans.Client.Rendering.World.VAOManager;
import Hilligans.Data.Other.BoundingBox;
import Hilligans.Entity.Entities.ItemEntity;
import Hilligans.Entity.Entity;
import Hilligans.Entity.LivingEntity;
import Hilligans.Network.PacketData;
import Hilligans.ServerMain;
import Hilligans.Util.Vector5f;
import Hilligans.World.ServerWorld;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class PlayerEntity extends LivingEntity {

    int textureId = -1;
    int verticesCount;

    public static int imageId;

    public PlayerEntity(float x, float y, float z,int id) {
        super(x,y,z,id);
        type = 0;
        boundingBox = new BoundingBox(-0.5f,-2.0f,-0.5f,0.5f,0.0f,0.5f);
    }

    public PlayerEntity(PacketData packetData) {
        super(packetData);
    }

    @Override
    public void tick() {
        //System.out.println("yes");
        for(Entity entity : ServerMain.world.entities.values()) {
            if(entity instanceof ItemEntity) {
                if (entity.boundingBox.intersectsBox(boundingBox, new Vector3f(entity.x, entity.y, entity.z), new Vector3f(x, y, z))) {
                    ServerMain.world.removeEntity(entity.id);
                }
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack) {
        if(textureId == -1) {
            createMesh();
        }
        GL30.glBindTexture(GL_TEXTURE_2D,imageId);
        GL30.glBindVertexArray(textureId);

        matrixStack.translate(x,y,z);
        matrixStack.rotate(-yaw,new Vector3f(0,1,0));
        matrixStack.rotate(pitch,new Vector3f(0,0,1));
        matrixStack.applyTransformation();
        glDrawElements(GL_TRIANGLES, verticesCount,GL_UNSIGNED_INT,0);
    }

    @Override
    public void destroy() {
        if(id != -1) {
            VAOManager.destroyBuffer(id);
        }
        super.destroy();
    }

    private void createMesh() {
        ArrayList<Vector5f> vector5fs = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for(int x = 0; x < 6; x++) {
            vector5fs.addAll(Arrays.asList(getVertices(x)));
            indices.addAll(Arrays.asList(getIndices(x,x * 4)));
        }

        float[] wholeMesh = new float[vector5fs.size() * 5];
        int[] wholeIndices = new int[indices.size()];
        int x = 0;
        for(Vector5f vector5f : vector5fs) {
            vector5f.addToList(wholeMesh,x * 5);
            x++;
        }
        x = 0;
        for(Integer a : indices) {
            wholeIndices[x] = a;
            x++;
        }
        verticesCount = wholeMesh.length;
        textureId = VAOManager.createVAO(wholeMesh,wholeIndices);
    }

    private Vector5f[] getVertices(int side) {

        int id = 4;
        float minX = 0.25f;
        float maxX = 0.5f;
        float minY = 0;
        float maxY = 1;

        if(side == 2) {
            minX = 0;
            maxX = 0.25f;
        }

        switch (side)  {
            case 0:
                return new Vector5f[] { new Vector5f(0.5f,0.5f,-0.5f,maxX,maxY),
                        new Vector5f(0.5f, -0.5f, -0.5f, maxX,minY),
                        new Vector5f(-0.5f,-0.5f,-0.5f,minX,minY),
                        new Vector5f(-0.5f,0.5f,-0.5f,minX,maxY)};
            case 1:
                return new Vector5f[] { new Vector5f(0.5f,0.5f,0.5f,maxX,maxY),
                        new Vector5f(0.5f, -0.5f, 0.5f, maxX,minY),
                        new Vector5f(-0.5f,-0.5f,0.5f,minX,minY),
                        new Vector5f(-0.5f,0.5f,0.5f,minX,maxY)};
            case 2:
                return new Vector5f[] { new Vector5f(-0.5f,0.5f,0.5f,maxX,maxY),
                        new Vector5f(-0.5f,-0.5f,0.5f,maxX,minY),
                        new Vector5f(-0.5f,-0.5f,-0.5f,minX,minY),
                        new Vector5f(-0.5f,0.5f,-0.5f,minX,maxY)};
            case 3:
                return new Vector5f[] { new Vector5f(0.5f,0.5f,0.5f,maxX,maxY),
                        new Vector5f(0.5f,-0.5f,0.5f,maxX,minY),
                        new Vector5f(0.5f,-0.5f,-0.5f,minX,minY),
                        new Vector5f(0.5f,0.5f,-0.5f,minX,maxY)};
            case 5:
                return new Vector5f[] { new Vector5f(0.5f,0.5f,0.5f,minX,minY),
                        new Vector5f(0.5f,0.5f,-0.5f,maxX,minY),
                        new Vector5f(-0.5f,0.5f,-0.5f,maxX,maxY),
                        new Vector5f(-0.5f,0.5f,0.5f,minX,maxY)};
            default:
                return new Vector5f[] { new Vector5f(0.5f,-0.5f,0.5f,minX,minY),
                        new Vector5f(0.5f,-0.5f,-0.5f,maxX,minY),
                        new Vector5f(-0.5f,-0.5f,-0.5f,maxX,maxY),
                        new Vector5f(-0.5f,-0.5f,0.5f,minX,maxY)};
        }
    }

    public Integer[] getIndices(int side, int spot) {
        switch (side) {
            case 0:
            case 5:
            case 3:
                return new Integer[] {spot,spot + 1,spot + 2,spot,spot + 2,spot + 3};
            default:
                return new Integer[]{spot,spot + 2, spot + 1, spot, spot + 3, spot + 2};
        }
    }


}
