package dev.Hilligans.ourcraft.Item;

import dev.Hilligans.ourcraft.Client.MatrixStack;
import dev.Hilligans.ourcraft.Client.Rendering.NewRenderer.PrimitiveBuilder;
import dev.Hilligans.ourcraft.Client.Rendering.World.Managers.ShaderManager;
import dev.Hilligans.ourcraft.Client.Rendering.World.Managers.VAOManager;
import dev.Hilligans.ourcraft.Client.Rendering.World.StringRenderer;
import dev.Hilligans.ourcraft.ClientMain;
import dev.Hilligans.ourcraft.Data.Other.ItemProperties;
import dev.Hilligans.ourcraft.Entity.LivingEntities.PlayerEntity;
import dev.Hilligans.ourcraft.GameInstance;
import dev.Hilligans.ourcraft.ModHandler.Content.ModContent;
import dev.Hilligans.ourcraft.Ourcraft;
import dev.Hilligans.ourcraft.Recipe.IRecipeComponent;
import dev.Hilligans.ourcraft.Util.Registry.IRegistryElement;
import dev.Hilligans.ourcraft.Util.Settings;
import dev.Hilligans.ourcraft.Util.Side;
import dev.Hilligans.ourcraft.World.World;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Item implements IRecipeComponent, IRegistryElement {

    public String name;
    public ModContent source;
    public ItemProperties itemProperties;
    public int id;
    public String modID;

    public int vao = -1;
    public int vertexCount = -1;

    public Item(String name, ItemProperties itemProperties) {
        this.name = name;
        this.itemProperties = itemProperties;
        this.modID = Ourcraft.GAME_INSTANCE.MOD_LOADER.mod;
        id = Items.getNextId();
    }

    public Item(String name, ItemProperties itemProperties, String modID) {
        this(name,itemProperties);
        this.modID = modID;
    }

    public Item setModContent(ModContent modContent) {
        this.source = modContent;
        return this;
    }

    public void generateTextures() {
        if(itemProperties.itemTextureManager != null) {
            itemProperties.itemTextureManager.generate();
        }
    }

    public void render(MatrixStack matrixStack, int x, int y, int size, ItemStack itemStack) {
        size *= 2;
        size -= Settings.guiSize * 2;
        x += Settings.guiSize;
        y += Settings.guiSize;
        glUseProgram(ClientMain.getClient().shaderManager.colorShader);
        glDisable(GL_DEPTH_TEST);
        if(itemProperties.dynamicModel || vao == -1) {
            PrimitiveBuilder primitiveBuilder = new PrimitiveBuilder(GL_TRIANGLES, ShaderManager.worldShader);
            addData(primitiveBuilder, 1);
            vertexCount = primitiveBuilder.indices.size();
            vao = VAOManager.createVAO(primitiveBuilder);
        }
        matrixStack.push();
        GL30.glBindVertexArray(vao);
        glBindTexture(GL_TEXTURE_2D, ClientMain.getClient().texture);

        matrixStack.translate(x,y,0);
        matrixStack.scale(size);
        matrixStack.applyTransformation(ClientMain.getClient().shaderManager.colorShader);
        glDrawElements(GL_TRIANGLES, vertexCount,GL_UNSIGNED_INT,0);
        matrixStack.pop();
        if(itemProperties.dynamicModel) {
            VAOManager.destroyBuffer(vao);
        }
        glEnable(GL_DEPTH_TEST);
        drawString(matrixStack,x - size / 2,y,size/2,itemStack.count);
    }

    public void renderHolding(MatrixStack matrixStack, int size, ItemStack itemStack) {
        int x = ClientMain.getWindowX() / 16 * 12;
        int y = ClientMain.getWindowY() / 16 * 12;

        size *= 2;
        size -= Settings.guiSize * 2;
        x += Settings.guiSize;
        y += Settings.guiSize;
        glUseProgram(ClientMain.getClient().shaderManager.colorShader);
        glDisable(GL_DEPTH_TEST);
        if(itemProperties.dynamicModel || vao == -1) {
            PrimitiveBuilder primitiveBuilder = new PrimitiveBuilder(GL_TRIANGLES, ShaderManager.worldShader);
            addData(primitiveBuilder, 1);
            vertexCount = primitiveBuilder.indices.size();
            vao = VAOManager.createVAO(primitiveBuilder);
        }
        matrixStack.push();
        GL30.glBindVertexArray(vao);
        glBindTexture(GL_TEXTURE_2D, ClientMain.getClient().texture);

        matrixStack.translate(x,y,-250);
        matrixStack.scale(size);
        matrixStack.rotate((float) Math.toRadians(ClientMain.getClient().renderTime / 25),new Vector3f(0,1,0));
        matrixStack.applyTransformation(ClientMain.getClient().shaderManager.colorShader);
        matrixStack.translate(0,0,-5);
        glDrawElements(GL_TRIANGLES, vertexCount,GL_UNSIGNED_INT,0);
        matrixStack.pop();
        if(itemProperties.dynamicModel) {
            VAOManager.destroyBuffer(vao);
        }
        glEnable(GL_DEPTH_TEST);
        drawString(matrixStack,x - size / 2,y,size/2,itemStack.count);
    }

    public void addData(PrimitiveBuilder primitiveBuilder, float size) {
        itemProperties.itemModel.addData(primitiveBuilder,itemProperties.itemTextureManager,0,size,null,0,0);
    }

    public boolean onActivate(World world, PlayerEntity playerEntity) {
        return true;
    }

    void drawString(MatrixStack matrixStack, int x, int y, int size, int count) {
        if(count != 1) {
            if(count >= 10) {
                StringRenderer.drawString(matrixStack, count + "", x + size + 14 , (int) (y + size * 1f), 0.5f);
            } else {
                StringRenderer.drawString(matrixStack, count + "", (int) (x + size) + 29, (int) (y + size * 1f), 0.5f);
            }
        }
    }

    public String getName() {
        return "item." + modID + "." + name;
    }
    public String getBlockName() {
        return "block." + modID + "." + name;
    }


    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public String getComponentName() {
        return "item";
    }

    @Override
    public void load(GameInstance gameInstance) {
        if(source.gameInstance.side == Side.CLIENT) {
            generateTextures();
        }
    }

    @Override
    public String getResourceName() {
        return name;
    }
}
