package com.aureskull.zmcmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
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
            .dimensions(width / 2 - 205, 20, 200, 20)
            .build();

    @Override
    protected void init() {
        super.init();

        //TODO : Faire en sorte que l'on puisse saisir un "e" car cette touche ferme le GUI
        this.mapNameTextField = new TextFieldWidget(this.textRenderer, 50,  50, 100, 20, Text.of(this.handler.getMapName()));
        /*this.mapNameTextField.setChangedListener((newText) -> {
            this.handler.blockEntity.mapName = newText; // Mettre à jour la valeur de mapName
        });*/

        this.addDrawableChild(this.mapNameTextField);

        saveMapName.setPosition(width / 2 - 205, 20);
        addDrawableChild(saveMapName);
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
}
