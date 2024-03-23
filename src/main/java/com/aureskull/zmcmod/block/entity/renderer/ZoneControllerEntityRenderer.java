package com.aureskull.zmcmod.block.entity.renderer;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.SmallZombieDoorwayBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ZoneControllerEntityRenderer implements BlockEntityRenderer<ZoneControllerBlockEntity> {

    public ZoneControllerEntityRenderer(BlockEntityRendererFactory.Context context){
    }

    @Override
    public void render(ZoneControllerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        drawLinesToDoorways(entity, matrices);

        BlockPos posA = entity.posA;
        BlockPos posB = entity.posB;

        if(posA == null || posB == null) return;

        int x1 = posA.getX();
        int x2 = posB.getX();

        int y1 = posA.getY();
        int y2 = posB.getY();

        int z1 = posA.getZ();
        int z2 = posB.getZ();


        if(x1 < x2)
            x2+=1;
        else if(x1 > x2)
            x1+=1;

        if(y1 < y2)
            y2+=1;
        else if(y1 > y2)
            y1+=1;

        if(z1 < z2)
            z2+=1;
		else if(z1 > z2)
			z2-=1;

        final int f_x1 = x1;
        final int f_x2 = x2;
        final int f_y1 = y1;
        final int f_y2 = y2;
        final int f_z1 = z1;
        final int f_z2 = z2;

        renderLine(entity, matrices, f_x1, f_x2, f_y1, f_y2, f_z1, f_z2);
        renderSide(entity, matrices, f_x1, f_x2, f_y1, f_y2, f_z1, f_z2);

        renderCube(entity, matrices, entity.posA);
        renderCube(entity, matrices, entity.posB);
    }

    public void renderLine(ZoneControllerBlockEntity entity, MatrixStack matrices, int f_x1, int f_x2, int f_y1, int f_y2, int f_z1, int f_z2){
        matrices.push();
        RenderSystem.enableDepthTest();
        matrices.translate(-entity.getPos().getX(), -entity.getPos().getY(), -entity.getPos().getZ());

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        //Arrêtes verticales
        buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();

        buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();

        buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();

        buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();

        //Arrêtes horrizontales
        buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();

        buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();

        buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();

        buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();

        buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();

        buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();

        buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();

        buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(entity.red, entity.green, entity.blue, 1f).next();
        buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(entity.red, entity.green, entity.blue, 1f).next();

        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        //Sans l'image, on ne peut pas afficher les faces du cube => A corriger
        RenderSystem.setShaderTexture(0, new Identifier("zmcmod", "test.png"));
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableCull();

        tessellator.draw();

        RenderSystem.enableCull();
        matrices.pop();
    }

    public void renderSide(ZoneControllerBlockEntity entity, MatrixStack matrices, int f_x1, int f_x2, int f_y1, int f_y2, int f_z1, int f_z2){
        matrices.push();
        RenderSystem.enableDepthTest();
        matrices.translate(-entity.getPos().getX(), -entity.getPos().getY(), -entity.getPos().getZ());

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        //Faces du cube
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        //float alpha = .11f;
        float alpha = .11f;
        RenderSystem.enableBlend(); // Enable blending

        buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();

        buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();

        buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();

        buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();

        buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();

        buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(entity.red, entity.green, entity.blue, alpha).next();

        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        //Sans l'image, on ne peut pas afficher les faces du cube => A corriger
        RenderSystem.setShaderTexture(0, new Identifier("zmcmod", "test.png"));
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.depthMask(true);
        matrices.pop();
    }

    public void renderCube(ZoneControllerBlockEntity entity, MatrixStack matrices, BlockPos blockPos){
        //Render entire cube including faces and lines
        matrices.push();
        RenderSystem.enableDepthTest();
        matrices.translate(-entity.getPos().getX(), -entity.getPos().getY(), -entity.getPos().getZ());

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();

        //Faces du cube
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        float alpha = .30f;
        RenderSystem.enableBlend();

        buffer.vertex(positionMatrix, x, y+1, z).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x+1, y+1, z).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x+1, y, z).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x, y, z).color(entity.red, entity.green, entity.blue, alpha).next();

        buffer.vertex(positionMatrix, x, y+1, z+1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x+1, y+1, z+1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x+1, y, z+1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x, y, z+1).color(entity.red, entity.green, entity.blue, alpha).next();

        buffer.vertex(positionMatrix, x+1, y+1, z+1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x, y+1, z+1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x, y+1, z).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x+1, y+1, z).color(entity.red, entity.green, entity.blue, alpha).next();

        buffer.vertex(positionMatrix, x+1, y, z+1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x, y, z+1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x, y, z).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x+1, y, z).color(entity.red, entity.green, entity.blue, alpha).next();

        buffer.vertex(positionMatrix, x+1, y+1, z+1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x+1, y+1, z).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x+1, y, z).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x+1, y, z+1).color(entity.red, entity.green, entity.blue, alpha).next();

        buffer.vertex(positionMatrix, x, y+1, z+1).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x, y+1, z).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x, y, z).color(entity.red, entity.green, entity.blue, alpha).next();
        buffer.vertex(positionMatrix, x, y, z+1).color(entity.red, entity.green, entity.blue, alpha).next();

        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        //Sans l'image, on ne peut pas afficher les faces du cube => A corriger
        RenderSystem.setShaderTexture(0, new Identifier("zmcmod", "test.png"));
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.depthMask(true);
        matrices.pop();
    }

    public void drawLinesToDoorways(ZoneControllerBlockEntity entity, MatrixStack matrices){
        if(entity.getAllLinkedBlocks(SmallZombieDoorwayBlockEntity.class).size() == 0) return;

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();


        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        for (BlockPos posB: entity.getAllLinkedBlocks(SmallZombieDoorwayBlockEntity.class)) {
            double deltaX = posB.getX() - entity.getPos().getX();
            double deltaY = posB.getY() - entity.getPos().getY();
            double deltaZ = posB.getZ() - entity.getPos().getZ();

            buffer.vertex(positionMatrix, 0.5F, .5F, 0.5F).color(0f, 1f, 0f, 1f).next();
            buffer.vertex(positionMatrix, (float) deltaX + 0.5F, (float) deltaY + 1.0F, (float) deltaZ + 0.5F).color(1f, 0f, 0f, 1f).next();
        }

        Tessellator.getInstance().draw();
    }
}
