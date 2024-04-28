package com.aureskull.zmcmod.screen.door;

import com.aureskull.zmcmod.screen.ScreenConstants;
import com.aureskull.zmcmod.screen.zonecontroller.ZoneControllerScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class DoorScreen extends HandledScreen<DoorScreenHandler> {

    private TextFieldWidget priceTextField;
    public ButtonWidget saveButtonWidget = ButtonWidget.builder(Text.literal("Save"), button -> {
                try{
                    int newPrice = Integer.parseInt(priceTextField.getText());
                    handler.updatePrice(newPrice);
                }catch (Exception e){

                }
            })
            .dimensions(0, 0, 45, ScreenConstants.BUTTON_HEIGHT)
            .build();

    public DoorScreen(DoorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        createElements();
        addElements();

        priceTextField.setText(String.valueOf(this.handler.doorBlockEntity.getPrice()));

        priceTextField.setPosition(width/2 - (priceTextField.getWidth()/2 + saveButtonWidget.getWidth()/2), height/2 - priceTextField.getHeight());
        saveButtonWidget.setPosition(priceTextField.getX() + priceTextField.getWidth(), height/2 - priceTextField.getHeight() - 1);

        // Supprimez les composants de texte par défaut
        this.titleX = Integer.MIN_VALUE;
        this.titleY = Integer.MIN_VALUE;
        this.playerInventoryTitleX = Integer.MIN_VALUE;
        this.playerInventoryTitleY = Integer.MIN_VALUE;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Price:"), priceTextField.getX(), priceTextField.getY() - 15, 0xffffff);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    private void createElements(){
        priceTextField = new TextFieldWidget(this.textRenderer, 0,  0, 50, ScreenConstants.TEXTFIELD_HEIGHT, Text.of(String.valueOf(this.handler.doorBlockEntity.getPrice())));
    }

    private void addElements(){
        addDrawableChild(priceTextField);
        addDrawableChild(saveButtonWidget);
    }
}
