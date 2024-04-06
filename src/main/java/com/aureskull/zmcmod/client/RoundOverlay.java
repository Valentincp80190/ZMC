package com.aureskull.zmcmod.client;

import com.aureskull.zmcmod.ZMCMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;

public class RoundOverlay implements HudRenderCallback {
    private final Identifier texture = new Identifier(ZMCMod.MOD_ID, "round/round_digit_1");

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            // Retrieve the width and height of the window
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            // Texture dimensions
            int textureWidth = 24; // The width of the texture in pixels
            int textureHeight = 48; // The height of the texture in pixels

            // Calculate the starting x and y coordinates to center the texture
            int x = textureWidth;
            int y = (screenHeight - textureHeight);

            // Set shader and color
            RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            // Bind the texture
            RenderSystem.setShaderTexture(0, texture);

            // Draw the texture at the calculated position
            drawContext.drawGuiTexture(texture, 5, y - 5, textureWidth, textureHeight);
        }
    }
}
