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

import java.util.ArrayList;
import java.util.List;

public class MoneyOverlay implements HudRenderCallback {
    private static MoneyOverlay instance;

    private final Identifier background = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/score/score.png");

    private int tmpMoney;
    private List<List<Integer>> moneyAnim;
    private final int MAX_MONEY_ANIM_TIME = 300;
    private int tmpTick = 0;

    private MoneyOverlay() {
        moneyAnim = new ArrayList<>();
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

            //int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            manageMoneyAnimation(screenHeight, drawContext);

            drawContext.drawText(client.textRenderer, PlayerData.getMoney() <= 99999999 ? String.valueOf(PlayerData.getMoney()) : "Too rich !", 13, screenHeight / 2 + screenHeight / 4 + 4, 0xFFFFFF, true);
            OverlayHelper.drawTexture(drawContext, background, 64, 16, 5, screenHeight / 2 + screenHeight / 4, .7f, 0f, 0f, .4f);
        }
    }

    private void manageMoneyAnimation(int screenHeight, DrawContext drawContext) {
        if (PlayerData.getMoney() != tmpMoney) {
            List<Integer> moneyInfos = new ArrayList<>();
            moneyInfos.add(PlayerData.getMoney() - tmpMoney);//Amount
            moneyInfos.add(30);//x
            moneyInfos.add(screenHeight / 2 + screenHeight / 4);//y
            moneyInfos.add(0);//display counter
            moneyInfos.add(100);//alpha

            if (PlayerData.getMoney() < tmpMoney)
                moneyInfos.add(0);//0 if -, 1 if +
            else
                moneyInfos.add(1);//0 if -, 1 if +

            moneyAnim.add(moneyInfos);
            tmpMoney = PlayerData.getMoney();
        }

        if (moneyAnim.size() > 0) {

            if(moneyAnim.get(0).get(4) <= 20)
                moneyAnim.remove(0);

            renderMoneyUpdates(drawContext);
        }
    }

    private void renderMoneyUpdates(DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();

        for(List<Integer> list : moneyAnim){
            if(list.get(4) <= 20) continue;

            int alphaChannel = (int) (255 * (float) list.get(4)/100) << 24;
            drawContext.drawText(client.textRenderer, list.get(5) == 1 ? "+" + String.valueOf(list.get(0)) : String.valueOf(list.get(0)), list.get(1), list.get(2), list.get(5) == 1 ? 0xFFFF00 | alphaChannel : 0xFF0000 | alphaChannel, true);

            list.set(1, list.get(1) + 1);

            if(list.get(5) == 0)
                list.set(2, list.get(2) + 1);
            else
                list.set(2, list.get(2) - 1);

            list.set(3, list.get(3)+1);
            list.set(4, list.get(4) - 3);
        }
    }
}
