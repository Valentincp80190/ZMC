package com.aureskull.zmcmod.client.overlay;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.util.PlayerData;
import com.aureskull.zmcmod.util.StateSaverAndLoader;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class MoneyOverlay implements HudRenderCallback {
    private static MoneyOverlay instance;

    private final Identifier background = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/score/score.png");


    private MoneyOverlay() {
    }

    public static MoneyOverlay getInstance() {
        if (instance == null) {
            instance = new MoneyOverlay();
        }
        return instance;
    }


    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client != null &&
            client.world != null &&
            client.player != null &&
            PlayerData.displayHUD &&
            PlayerData.getGameUUID() != null) {

            if (client.isPaused())
                return;

            TextRenderer textRenderer = client.textRenderer;

            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            drawContext.drawText(textRenderer, PlayerData.getMoney() <= 99999999 ? String.valueOf(PlayerData.getMoney()) : "Too rich !", screenWidth-64 + 10, screenHeight/2 + screenHeight/4 + 4, 0xFFFFFF, true);
            OverlayHelper.drawTexture(drawContext, background, 64, 16, screenWidth-64, screenHeight/2 + screenHeight/4, .7f, 0f, 0f, .4f);
        }
    }
}
