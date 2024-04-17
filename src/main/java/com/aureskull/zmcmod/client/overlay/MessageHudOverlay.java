package com.aureskull.zmcmod.client.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MessageHudOverlay implements HudRenderCallback {
    private static String message = "";
    private static long displayUntil = 0;
    public static Formatting formatting = Formatting.WHITE;

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if(message.isEmpty() || !(System.currentTimeMillis() < displayUntil)) return;

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int textWidth = textRenderer.getWidth(message);

        drawContext.drawText(textRenderer, Text.literal(message).formatted(formatting), (screenWidth - textWidth) / 2, screenHeight - 50, 0xFFFFFF, true);
    }

    public static void setMessage(String pMessage, Formatting p_formatting, int displayDurationMilliseconds){
        message = pMessage;
        displayUntil = System.currentTimeMillis() + displayDurationMilliseconds;
        formatting = p_formatting;
    }
}
