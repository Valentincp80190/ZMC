package com.aureskull.zmcmod.client;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.util.PlayerData;
import com.aureskull.zmcmod.util.StateSaverAndLoader;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class RoundOverlay implements HudRenderCallback {
    private final Identifier DIGIT_0 = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/round/round_digit_0.png");
    private final Identifier DIGIT_1 = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/round/round_digit_1.png");
    private final Identifier DIGIT_2 = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/round/round_digit_2.png");
    private final Identifier DIGIT_3 = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/round/round_digit_3.png");
    private final Identifier DIGIT_4 = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/round/round_digit_4.png");
    private final Identifier DIGIT_5 = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/round/round_digit_5.png");
    private final Identifier DIGIT_6 = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/round/round_digit_6.png");
    private final Identifier DIGIT_7 = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/round/round_digit_7.png");
    private final Identifier DIGIT_8 = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/round/round_digit_8.png");
    private final Identifier DIGIT_9 = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/round/round_digit_9.png");
    private final Identifier TALLY_MARKS = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/round/round_tally_marks.png");

    private Identifier texture1 = DIGIT_1;
    private Identifier texture2 = null;
    private Identifier texture3 = null;


    private static int currentRoundNumber = 0;
    private int tempRoundNumber = 0;

    private float alpha = 0f;
    private float green = 0f;
    private float blue = 0f;


    private boolean increasing = false; // Direction of alpha change
    private long lastUpdateTickTime = 0; // Track time to manage the rate of alpha change
    private boolean flashing = false;
    private final int FLASHING_TICK_TIME = 600;
    private int lastFlashingTickTime = 0;

    //Flash during N tick / wait 2 seconds / Appear the round

    private boolean wait_two_seconds = false;
    private final int WAIT_TWO_SECONDS_TICK = 140;
    private int lastWaitForTwoSeconds = 0;

    public RoundOverlay() {
    }


    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (currentRoundNumber > 0 && client != null && client.world != null && client.player != null && PlayerData.displayHUD) {
            if(currentRoundNumber > 1 && currentRoundNumber != tempRoundNumber){
                flashing = true;
            }

            tempRoundNumber = currentRoundNumber;

            if (client.isPaused())
                return;

            int windowWidth = client.getWindow().getScaledWidth();
            int windowHeight = client.getWindow().getScaledHeight();

            drawRoundNumber(drawContext, windowWidth, windowHeight);
        }
    }

    private void drawRoundNumber(DrawContext drawContext, int windowWidth, int windowHeight){
        if(flashing){
            if(lastUpdateTickTime >= 4){
                if(green < 1f){
                    green = green + .1f;
                    if(green > 1f) green = 1f;
                }

                if(blue < 1f){
                    blue = blue + .1f;
                    if(blue > 1f) blue = 1f;
                }

                if (increasing) {
                    alpha += .1f;
                    if (alpha >= 1.0f) {
                        alpha = 1.0f;
                        increasing = false;
                    }
                } else {
                    alpha -= .1f;
                    if (alpha <= 0.1f) {
                        alpha = 0.1f;
                        increasing = true;
                    }
                }
                lastUpdateTickTime = 0;
            }else lastUpdateTickTime++;

            if(lastFlashingTickTime >= FLASHING_TICK_TIME){
                flashing = false;
                lastFlashingTickTime = 0;
                lastUpdateTickTime = 0;

                wait_two_seconds = true;
            }else lastFlashingTickTime++;
        }

        if(wait_two_seconds){
            lastWaitForTwoSeconds++;
            if(alpha >= 0f){
                alpha -= 0.025;
                if(alpha < 1)
                    alpha = 0f;
            }

            if(lastWaitForTwoSeconds >= WAIT_TWO_SECONDS_TICK){
                wait_two_seconds = false;
                lastWaitForTwoSeconds = 0;

                //update round texture
                refreshRoundTextures();
            }
        }

        //Smooth increase
        if(!flashing && !wait_two_seconds){
            if(lastUpdateTickTime >= 6){
                if(alpha < 1f){
                    alpha += 0.1;
                    if(alpha > 1)
                        alpha = 1f;
                }

                if(green > 0f){
                    green = green - .07f;
                    if(green < 0f) green = 0f;
                }

                if(blue > 0f){
                    blue = blue - .07f;
                    if(blue < 0f) blue = 0f;
                }


                lastUpdateTickTime = 0;
            }else lastUpdateTickTime++;
        }

        if(texture1 != null) drawSingleTexture(drawContext, texture1, 20, windowHeight - 50, 1f, green, blue, alpha);
        if(texture2 != null) drawSingleTexture(drawContext, texture2, 20 + 46/2, windowHeight - 50, 1f, green, blue, alpha);
        if(texture3 != null) drawSingleTexture(drawContext, texture3, 20 + 46, windowHeight - 50, 1f, green, blue, alpha);
    }

    private void drawSingleTexture(DrawContext drawContext, Identifier texture, int x, int y, float p_red, float p_green, float p_blue, float p_alpha) {
        Matrix4f positionMatrix = drawContext.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(positionMatrix, x, y, 0).color(p_red, p_green, p_blue, p_alpha).texture(0f, 0f).next();
        buffer.vertex(positionMatrix, x, y + 46, 0).color(p_red, p_green, p_blue, p_alpha).texture(0f, 1f).next();
        buffer.vertex(positionMatrix, x + 23, y + 46, 0).color(p_red, p_blue, p_blue, p_alpha).texture(1f, 1f).next();
        buffer.vertex(positionMatrix, x + 23, y, 0).color(p_red, p_green, p_blue, p_alpha).texture(1f, 0f).next();

        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        tessellator.draw();
        RenderSystem.disableBlend();
    }

    private void refreshRoundTextures() {
        String roundStr = String.valueOf(currentRoundNumber);
        int length = roundStr.length();

        // Reset textures
        texture1 = null;
        texture2 = null;
        texture3 = null;

        // Assign textures based on the number of digits in the round number
        if (length == 1) {
            texture1 = getTextureForDigit(roundStr.charAt(length - 1) - '0');

        }else if (length == 2) {
            texture2 = getTextureForDigit(roundStr.charAt(length - 1) - '0');
            texture1 = getTextureForDigit(roundStr.charAt(length - 2) - '0');

        }else if(length == 3) {
            texture3 = getTextureForDigit(roundStr.charAt(length - 1) - '0');
            texture2 = getTextureForDigit(roundStr.charAt(length - 2) - '0');
            texture1 = getTextureForDigit(roundStr.charAt(length - 3) - '0');
        }
    }

    private Identifier getTextureForDigit(int digit) {
        switch (digit) {
            case 0: return DIGIT_0;
            case 1: return DIGIT_1;
            case 2: return DIGIT_2;
            case 3: return DIGIT_3;
            case 4: return DIGIT_4;
            case 5: return DIGIT_5;
            case 6: return DIGIT_6;
            case 7: return DIGIT_7;
            case 8: return DIGIT_8;
            case 9: return DIGIT_9;
            default: return null; // Fallback case, although this should not happen
        }
    }

    public static void updateRoundNumber(int newRoundNumber) {
        currentRoundNumber = newRoundNumber;
    }
}
