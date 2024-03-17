package com.aureskull.zmcmod.block.entity.renderer;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.SmallZombieDoorwayBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class SmallZombieDoorwayEntityRenderer  implements BlockEntityRenderer<SmallZombieDoorwayBlockEntity> {
    //TODO : Dessiner une texture de 1*3 pixels qui représente un fil

    public SmallZombieDoorwayEntityRenderer(BlockEntityRendererFactory.Context context){

    }

    @Override
    public void render(SmallZombieDoorwayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(entity.getLinkedSpawner() != null){
            renderLine(entity.getPos(), entity.getLinkedSpawner(), matrices);
        }
    }

    private void renderLine(BlockPos posA, BlockPos posB, MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();

        double deltaX = posB.getX() - posA.getX();
        double deltaY = posB.getY() - posA.getY();
        double deltaZ = posB.getZ() - posA.getZ();

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float red = 1.0F;
        float green = 0.0F;
        float blue = 0.0F;
        float alpha = 1.0F;

        buffer.vertex(positionMatrix, 0.5F, 1F, 0.5F).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, (float) deltaX + 0.5F, (float) deltaY + 0.5F, (float) deltaZ + 0.5F).color(red, green, blue, alpha).next();

        Tessellator.getInstance().draw();
    }

}
