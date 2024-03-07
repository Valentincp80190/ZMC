package com.aureskull.zmcmod.screen.zonecontroller;

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

@Environment(EnvType.CLIENT)
public class ZoneControllerScreen extends HandledScreen<ZoneControllerScreenHandler> {
    public final int PADDING = 4;
    public final int RIGHT_ELEMENT_MAX_SIZE = 110;
    public final int RIGHT_PADDING = 10;
    public final int TOP_PADDING = 10;
    public final int SECTION_PADDING_TEXT = 15;
    public final int SECTION_PADDING = 27;
    public final int SLIDER_WIDTH = RIGHT_ELEMENT_MAX_SIZE;
    public final int TEXTFIELD_WIDTH = 50;
    public final int TEXTFIELD_LEFT_PADDING = 15;
    public final int LEFT_TEXT_BLOCKPOS_OFFSET = 7;
    public final int TOP_TEXT_BLOCKPOS_OFFSET = 5;

    public ZoneControllerScreen(ZoneControllerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    public ButtonWidget posAXButtonWidget = ButtonWidget.builder(Text.literal("+ -"), button -> {
                //this.handler.updateMapName(mapNameTextField.getText());
                this.handler.blockEntity.posA.add(0, 1, 0);
                this.handler.blockEntity.posB.add(0, 1, 0);
                this.handler.blockEntity.markDirty();
            })
            .dimensions(0, 0, 20, 20)
            .build();


    public SliderWidget redSlider = new SliderWidget(0, 0, SLIDER_WIDTH, 20, Text.literal("Red: " + convertTo255( handler.getRed() )).styled(style -> style.withColor(TextColor.fromRgb(0xFF0000))), handler.getRed()) {
        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("Red: " + convertTo255( handler.getRed() )).styled(style -> style.withColor(TextColor.fromRgb(0xFF0000))));
        }

        @Override
        protected void applyValue() {
            handler.updateZoneColor((float)this.value, "red");
        }
    };

    public SliderWidget greenSlider = new SliderWidget(0, 0, SLIDER_WIDTH, 20, Text.literal("Green: " + convertTo255( handler.getGreen() )).styled(style -> style.withColor(TextColor.fromRgb(0x00FF00))), handler.getGreen()) {
        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("Green: " + convertTo255( handler.getGreen() )).styled(style -> style.withColor(TextColor.fromRgb(0x00FF00))));
        }

        @Override
        protected void applyValue() {
            handler.updateZoneColor((float)this.value, "green");
        }
    };

    public SliderWidget blueSlider = new SliderWidget(0, 0, SLIDER_WIDTH, 20, Text.literal("Blue: " + convertTo255( handler.getBlue() )).styled(style -> style.withColor(TextColor.fromRgb(0x0000FF))), handler.getBlue()) {
        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("Blue: " + convertTo255( handler.getBlue() )).styled(style -> style.withColor(TextColor.fromRgb(0x0000FF))));
        }

        @Override
        protected void applyValue() {
            handler.updateZoneColor((float)this.value, "blue");
        }
    };

    private TextFieldWidget posAXTextField;

    @Override
    protected void init() {
        super.init();
        createElements();
        addElements();


        redSlider.setPosition(width - redSlider.getWidth() - RIGHT_PADDING, 23);
        greenSlider.setPosition(width - redSlider.getWidth() - RIGHT_PADDING, redSlider.getY() + redSlider.getHeight() + PADDING);
        blueSlider.setPosition(width - redSlider.getWidth() - RIGHT_PADDING, greenSlider.getY() + greenSlider.getHeight() + PADDING);

        posAXTextField.setPosition(width - TEXTFIELD_WIDTH - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + TEXTFIELD_LEFT_PADDING, blueSlider.getHeight() + blueSlider.getY() + SECTION_PADDING);
        posAXButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH), blueSlider.getHeight() + blueSlider.getY() + SECTION_PADDING);

        // Supprimez les composants de texte par défaut
        this.titleX = Integer.MIN_VALUE;
        this.titleY = Integer.MIN_VALUE;
        this.playerInventoryTitleX = Integer.MIN_VALUE;
        this.playerInventoryTitleY = Integer.MIN_VALUE;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Zone color"), width - 81 - RIGHT_PADDING, TOP_PADDING, 0xffffff);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("First zone position"), width - 62 - RIGHT_PADDING, blueSlider.getY() + blueSlider.getHeight() + SECTION_PADDING_TEXT, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("X:"), width - RIGHT_PADDING - RIGHT_ELEMENT_MAX_SIZE + LEFT_TEXT_BLOCKPOS_OFFSET, blueSlider.getHeight() + blueSlider.getY() + SECTION_PADDING + TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Y:"), width - 60 - RIGHT_PADDING, blueSlider.getY() + blueSlider.getHeight() + SECTION_PADDING, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Z:"), width - 60 - RIGHT_PADDING, blueSlider.getY() + blueSlider.getHeight() + SECTION_PADDING, 0xffffff);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public int convertTo255(float value) {
        float clampedValue = Math.max(0f, Math.min(1f, value));
        return Math.round(clampedValue * 255);
    }

    private void createElements(){
        posAXTextField = new TextFieldWidget(this.textRenderer, 0,  0, TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.getPosA().getX())));
    }

    private void addElements(){
        addDrawableChild(posAXButtonWidget);

        addDrawableChild(redSlider);
        addDrawableChild(greenSlider);
        addDrawableChild(blueSlider);

        addDrawableChild(posAXTextField);
    }
}
