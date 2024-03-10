package com.aureskull.zmcmod.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class MessageHudOverlay implements HudRenderCallback {
    private static String message = "";
    private static long displayUntil = 0;

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if(message.isEmpty() || !(System.currentTimeMillis() < displayUntil)) return;

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int textWidth = textRenderer.getWidth(message);

        drawContext.drawText(textRenderer, message, (screenWidth - textWidth) / 2, screenHeight - 50, 0xFFFFFF, true);
    }

    public static void setMessage(String pMessage, int displayDurationMilliseconds){
        message = pMessage;
        displayUntil = System.currentTimeMillis() + displayDurationMilliseconds;
    }
}
