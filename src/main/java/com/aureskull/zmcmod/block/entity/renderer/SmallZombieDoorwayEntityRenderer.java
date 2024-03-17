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
import org.joml.Matrix4f;

public class SmallZombieDoorwayEntityRenderer  implements BlockEntityRenderer<SmallZombieDoorwayBlockEntity> {

    public SmallZombieDoorwayEntityRenderer(BlockEntityRendererFactory.Context context){

    }

    @Override
    public void render(SmallZombieDoorwayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(entity.getLinkedSpawner() != null){
            ZMCMod.LOGGER.info("DISPLAY LINE");
            renderLine(entity.getPos(), entity.getLinkedSpawner(), matrices);
        }
    }

    private void renderLine(BlockPos posA, BlockPos posB, MatrixStack matrices) {
        matrices.push();
        RenderSystem.enableDepthTest();
        matrices.translate(-posA.getX(), -posA.getY(), -posA.getZ());

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        int x1 = posA.getX();
        int x2 = posB.getX();

        int y1 = posA.getY();
        int y2 = posB.getY();

        int z1 = posA.getZ();
        int z2 = posB.getZ();

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);

        buffer.vertex(positionMatrix, x1, y1, z1).color(1f, 0f, 0f, 1f).next();
        buffer.vertex(positionMatrix, x2, y2, z2).color(0f, 1f, 0f, 1f).next();

        //Sans l'image, on ne peut pas afficher les faces du cube => A corriger
        RenderSystem.setShaderTexture(0, new Identifier("zmcmod", "test.png"));
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableCull();

        tessellator.draw();

        RenderSystem.enableCull();
        matrices.pop();
    }
}
