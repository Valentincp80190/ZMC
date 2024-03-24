package com.aureskull.zmcmod.screen.mapcontroller;

import com.aureskull.zmcmod.screen.ScreenConstants;
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
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class MapControllerScreen extends HandledScreen<MapControllerScreenHandler> {
    private TextFieldWidget mapNameTextField;
    private ButtonWidget startGameButton = ButtonWidget.builder(Text.literal("")
                    .append(Text.literal(this.handler.blockEntity.isStart() ? "STARTED" : "STOPPED")
                        .formatted(this.handler.blockEntity.isStart() ? Formatting.GREEN : Formatting.RED)), button -> {
                this.handler.blockEntity.setStart(!this.handler.blockEntity.isStart());

                this.startGameButton.setMessage(Text.literal("")
                        .append(Text.literal(this.handler.blockEntity.isStart() ? "STARTED" : "STOPPED")
                                .formatted(this.handler.blockEntity.isStart() ? Formatting.GREEN : Formatting.RED)));

            })
            .dimensions(0, 0, 65, ScreenConstants.BUTTON_HEIGHT)
            .build();


    public ButtonWidget saveMapNameButton = ButtonWidget.builder(Text.literal("Save"), button -> {
            this.handler.updateMapName(mapNameTextField.getText());
        })
        .dimensions(0, 0, 50, ScreenConstants.BUTTON_HEIGHT)
        .build();

    public MapControllerScreen(MapControllerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        createElements();
        addElements();

        mapNameTextField.setText(String.valueOf(this.handler.getMapName()));

        mapNameTextField.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET, 23);
        saveMapNameButton.setPosition(width - saveMapNameButton.getWidth() - ScreenConstants.RIGHT_PADDING, 23);

        startGameButton.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET, saveMapNameButton.getY() + saveMapNameButton.getHeight() + ScreenConstants.PADDING);


        // Supprimez les composants de texte par défaut
        this.titleX = Integer.MIN_VALUE;
        this.titleY = Integer.MIN_VALUE;
        this.playerInventoryTitleX = Integer.MIN_VALUE;
        this.playerInventoryTitleY = Integer.MIN_VALUE;
    }

    private void addElements(){
        addDrawableChild(mapNameTextField);
        addDrawableChild(saveMapNameButton);
        addDrawableChild(startGameButton);
    }

    private void createElements(){
        this.mapNameTextField = new TextFieldWidget(this.textRenderer, 0,  0, 45, ScreenConstants.TEXTFIELD_HEIGHT, Text.of(this.handler.getMapName()));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta); //Ne pas afficher l'inventaire etc
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Map: " + mapNameTextField.getText()), 10 + textRenderer.getWidth(Text.literal("Map: " + mapNameTextField.getText()))/2, 10, 0xffffff);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Map Information"), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE - ScreenConstants.RIGHT_PADDING + (textRenderer.getWidth(Text.literal("Map Information"))/2), ScreenConstants.TOP_PADDING, 0xffffff);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Name: "), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE - ScreenConstants.RIGHT_PADDING + (textRenderer.getWidth(Text.literal("Name: "))/2) + ScreenConstants.SECTION_PADDING_TEXT, ScreenConstants.SECTION_PADDING + ScreenConstants.TOP_TEXT_OFFSET_SECTION, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("State: "), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE - ScreenConstants.RIGHT_PADDING + (textRenderer.getWidth(Text.literal("State: "))/2) + ScreenConstants.SECTION_PADDING_TEXT, mapNameTextField.getHeight() + mapNameTextField.getY() + ScreenConstants.TOP_TEXT_OFFSET, 0xffffff);
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
