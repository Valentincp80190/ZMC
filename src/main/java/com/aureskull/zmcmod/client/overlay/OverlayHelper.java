package com.aureskull.zmcmod.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class OverlayHelper {
    public static void drawTexture(DrawContext drawContext, Identifier texture, int width, int height, int x, int y, float p_red, float p_green, float p_blue, float p_alpha) {
        Matrix4f positionMatrix = drawContext.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(positionMatrix, x, y, 0).color(p_red, p_green, p_blue, p_alpha).texture(0f, 0f).next();
        buffer.vertex(positionMatrix, x, y + height, 0).color(p_red, p_green, p_blue, p_alpha).texture(0f, 1f).next();
        buffer.vertex(positionMatrix, x + width, y + height, 0).color(p_red, p_blue, p_blue, p_alpha).texture(1f, 1f).next();
        buffer.vertex(positionMatrix, x + width, y, 0).color(p_red, p_green, p_blue, p_alpha).texture(1f, 0f).next();

        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        tessellator.draw();
        RenderSystem.disableBlend();
    }
}
