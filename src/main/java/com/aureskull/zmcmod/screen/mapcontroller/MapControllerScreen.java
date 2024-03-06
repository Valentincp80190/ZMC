package com.aureskull.zmcmod.screen.mapcontroller;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class MapControllerScreen extends HandledScreen<MapControllerScreenHandler> {
    private TextFieldWidget mapNameTextField;

    public MapControllerScreen(MapControllerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    public ButtonWidget saveMapName = ButtonWidget.builder(Text.literal("Save"), button -> {
            this.handler.updateMapName(mapNameTextField.getText());
        })
        .dimensions(width/2 + 45, 50, 50, 20)
        .build();

    @Override
    protected void init() {
        super.init();

        this.mapNameTextField = new TextFieldWidget(this.textRenderer, width/2 - 60,  51, 100, 18, Text.of(this.handler.getMapName()));

        this.addDrawableChild(this.mapNameTextField);

        saveMapName.setPosition(width/2 + 45, 50);
        addDrawableChild(saveMapName);

        // Supprimez les composants de texte par défaut
        this.titleX = Integer.MIN_VALUE;
        this.titleY = Integer.MIN_VALUE;
        this.playerInventoryTitleX = Integer.MIN_VALUE;
        this.playerInventoryTitleY = Integer.MIN_VALUE;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta); //Ne pas afficher l'inventaire etc
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Map name : " + this.handler.getMapName()), width / 2, height / 2, 0xffffff);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputUtil.GLFW_KEY_E) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
