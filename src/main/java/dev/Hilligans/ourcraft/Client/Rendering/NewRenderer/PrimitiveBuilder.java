package dev.Hilligans.ourcraft.Client.Rendering.NewRenderer;

import dev.Hilligans.ourcraft.Client.MatrixStack;
import dev.Hilligans.ourcraft.Client.Rendering.World.Managers.VAOManager;
import dev.Hilligans.ourcraft.Data.Primitives.FloatList;
import dev.Hilligans.ourcraft.Data.Primitives.IntList;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class PrimitiveBuilder {

    public FloatList vertices = new FloatList();
    public IntList indices = new IntList();

    public int type;
    public int size = 0;
    public int count = 0;

    public Shader shader;

    public PrimitiveBuilder(int type, Shader shader) {
        this.type = type;
        this.shader = shader;
    }

    public void add(float... floats) {
        count += floats.length;
        vertices.add(floats);
        indices.add(vertices.size());
        size++;
    }

    public void addMinus(float... floats) {
        count += floats.length;
        vertices.add(floats);
        size++;
    }

    public void add(float[] vertices, int[] indices) {
        count += vertices.length;
        this.vertices.add(vertices);
        this.indices.add(indices);
        size++;
    }

    public void add(int[] indices) {
        int count = sizeVal;
        for(int index : indices) {
            this.indices.add(index + count);
        }
    }

    public void addLargest(int[] indices) {
        int val = largest;
        for(int index : indices) {
            this.indices.add(index + val);
            if(index > largest) {
                largest = index;
            }
        }
    }
    public int sizeVal = 0;
    int largest = 0;

    public void addQuad(float... vertices) {
        int count = getVerticesCount();
        this.indices.add(count,count + 1, count + 2,count + 3, count + 2, count + 1);
        this.vertices.add(vertices);
        size+= 4;
    }

    public void buildQuad(float minX, float minY, float z, float minTexX, float minTexY, float maxX, float maxY, float maxTexX, float maxTexY) {
        addQuad(minX,minY,z,minTexX,minTexY,
                minX,maxY,z,minTexX,maxTexY,
                maxX,minY,z,maxTexX,minTexY,
                maxX,maxY,z,maxTexX,maxTexY);
    }

    public void addQuadIndices() {
        int count = getVerticesCount();
        this.indices.add(count,count + 1, count + 2,count + 3, count + 2, count + 1);
    }

    public void addQuadIndicesInverse() {
        int count = getVerticesCount();
        this.indices.add(count,count + 2, count + 1,count + 3, count + 1, count + 2);
    }

    public void addQuadInverse(float... vertices) {
        int count = getVerticesCount();
        this.indices.add(count,count + 2, count + 1,count + 3, count + 1, count + 2);
        this.vertices.add(vertices);
        size+= 4;
    }

    protected void translate(int startX, float x, float y, float z) {
        for(int i = startX; i < vertices.size(); i+= shader.shaderElementCount) {
            vertices.elementData[i] += x;
            vertices.elementData[i + 1] += y;
            vertices.elementData[i + 2] += z;
        }
    }

    public void translate(float x, float y, float z) {
        translate(0,x,y,z);
    }

    public int getVerticesCount() {
        return vertices.size() / shader.shaderElementCount;
    }

    public int[] createMesh() {
        return createMesh(GL_STATIC_DRAW);
    }

    public int[] createMesh(int mode) {
        float[] vertices = this.vertices.getElementData();
        int[] indices = this.indices.getElementData();

        int VAO = glGenVertexArrays();
        int VBO = glGenBuffers();
        int EBO = glGenBuffers();
        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, mode);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, mode);
        int x = 0;
        int pointer = 0;
        for(Shader.ShaderElement shaderElement : shader.shaderElements) {
            glVertexAttribPointer(x,shaderElement.count,shaderElement.type,shaderElement.normalised,shader.shaderElementCount * 4,pointer * 4);
            glEnableVertexAttribArray(x);
            x++;
            pointer += shaderElement.count;
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return new int[]{VAO,VBO,EBO};
    }

    public void draw(MatrixStack matrixStack) {
        id = VAOManager.createVAO(this, GL_STATIC_DRAW);
        GL30.glBindVertexArray(id);
        matrixStack.push();
        matrixStack.applyTransformation(shader.shader);
        glDrawElements(type, indices.size(), GL_UNSIGNED_INT, 0);
        matrixStack.pop();
        VAOManager.destroyBuffer(id);
    }

    public void draw1(MatrixStack matrixStack) {
        if(id == -1) {
            id = VAOManager.createVAO(this,GL_DYNAMIC_DRAW);
        } else {
            glBindBuffer(GL_ARRAY_BUFFER, VAOManager.buffers.get(id).typeA);
            glBufferData(GL_ARRAY_BUFFER,this.vertices.getElementData(),GL_STREAM_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, VAOManager.buffers.get(id).typeB);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER,this.indices.getElementData(),GL_STREAM_DRAW);
        }
        GL30.glBindVertexArray(id);
        matrixStack.push();
        matrixStack.applyTransformation(shader.shader);
        glDrawElements(type, indices.size(), GL_UNSIGNED_INT, 0);
        VAOManager.destroyBuffer(id);
        id = -1;
        matrixStack.pop();
    }

    public static int id = -1;

    public void draw(MatrixStack matrixStack, int drawMode) {
        float[] vals = new float[count];
        System.arraycopy(vertices.elementData,0,vals,0,count);
        //System.out.println(Arrays.toString(vals));
        //System.out.println(glGetError());
        int VAO = glGenVertexArrays();
        int VBO = glGenBuffers();

        glBindVertexArray(VAO);


        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vals, GL_STATIC_DRAW);

        int x = 0;
        int pointer = 0;
        for(Shader.ShaderElement shaderElement : shader.shaderElements) {
            //System.out.println(x + ":" + shaderElement.count + ":" + shaderElement.type + ":" + shaderElement.normalised +":" + shader.shaderElementCount * 4 + ":" + pointer * 4);
            glVertexAttribPointer(x,shaderElement.count,shaderElement.type,shaderElement.normalised,shader.shaderElementCount * 4,pointer * 4);
            //System.out.println(glGetVertexAttribPointer(0, GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING));
            //System.out.println(glGetError());

            glEnableVertexAttribArray(x);
            x++;
            pointer += shaderElement.count;
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        matrixStack.push();
        //System.out.println(glGetError());
        glUseProgram(shader.shader);

        GL30.glBindVertexArray(VAO);
        //System.out.println(glGetError());

        glDrawArrays(type, 0,size);
        //glDrawElements(type, size,GL_UNSIGNED_INT,0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        //System.out.println(glGetError());


        matrixStack.pop();
        glDeleteBuffers(VBO);
       // glDeleteBuffers(EBO);
        glDeleteVertexArrays(VAO);
    }

    public void drawTris(MatrixStack matrixStack) {
        float[] vals = new float[count];
        System.arraycopy(vertices.elementData,0,vals,0,count);

        int VAO = glGenVertexArrays();
        int VBO = glGenBuffers();
        //int EBO = glGenBuffers();

        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vals, GL_STREAM_DRAW);

        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        //glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.elementData, GL_STATIC_DRAW);

        int x = 0;
        int pointer = 0;
        for(Shader.ShaderElement shaderElement : shader.shaderElements) {
            glVertexAttribPointer(x,shaderElement.count,shaderElement.type,shaderElement.normalised,shader.shaderElementCount * 4,pointer * 4);
            glEnableVertexAttribArray(x);
            x++;
            pointer += shaderElement.count;
        }
        //glBindBuffer(GL_ARRAY_BUFFER, 0);

        matrixStack.push();
        glUseProgram(shader.shader);
        matrixStack.applyTransformation(shader.shader);
        GL30.glBindVertexArray(VAO);
        glDrawArrays(type,0,vertices.size());
        //glDrawElements(type, size + 8,GL_UNSIGNED_INT,0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(VBO);
        //glDeleteBuffers(EBO);
        matrixStack.pop();
        glDeleteVertexArrays(VAO);
    }

    public void rotate(float degrees, Vector3f vector) {
        rotate(degrees,vector,0);
    }

    public void rotate(float degrees, Vector3f vector, int startPos) {
        Matrix3f matrix3f = new Matrix3f(1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f);
        matrix3f.rotate(degrees,vector);
        for(int x = startPos; x < vertices.size(); x+=shader.shaderElementCount) {
            Vector3f vector3f = new Vector3f(vertices.elementData[x],vertices.elementData[x] + 1, vertices.elementData[x] + 2).mul(matrix3f);
            vertices.elementData[x] = vector3f.x;
            vertices.elementData[x + 1] = vector3f.y;
            vertices.elementData[x + 2] = vector3f.z;
        }
    }

    public void translate(float x, float y, float z, int startPos) {
        for(int i = startPos; i < vertices.size(); i+=shader.shaderElementCount) {
            vertices.elementData[i] += x;
            vertices.elementData[i + 1] += y;
            vertices.elementData[i + 2] += z;
        }
    }


    public void applyTransformation(Matrix4f matrix4f, int shaderId, String name) {
        int trans = glGetUniformLocation(shaderId, name);
        float[] floats = new float[16];;
        glUniformMatrix4fv(trans,false,matrix4f.get(floats));
    }

    public void applyTransformation(float windowX, float windowY, int shaderId, String name) {
        int trans = glGetUniformLocation(shaderId, name);
        glUniform2fv(trans,new float[]{windowX,windowY});
    }

    public static final int
            POINTS         = 0x0,
            LINES          = 0x1,
            LINE_LOOP      = 0x2,
            LINE_STRIP     = 0x3,
            TRIANGLES      = 0x4,
            TRIANGLE_STRIP = 0x5,
            TRIANGLE_FAN   = 0x6,
            QUADS          = 0x7,
            QUAD_STRIP     = 0x8,
            POLYGON        = 0x9;
}
