package com.aureskull.zmcmod.screen.zonecontroller;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

@Environment(EnvType.CLIENT)
public class ZoneControllerScreen extends HandledScreen<ZoneControllerScreenHandler> {

    public ZoneControllerScreen(ZoneControllerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    public ButtonWidget saveMapName = ButtonWidget.builder(Text.literal("Up line"), button -> {
                //this.handler.updateMapName(mapNameTextField.getText());
                this.handler.blockEntity.posA.add(0, 1, 0);
                this.handler.blockEntity.posB.add(0, 1, 0);
                this.handler.blockEntity.markDirty();
            })
            .dimensions(width/2 + 45, 50, 100, 20)
            .build();


    public SliderWidget redSlider = new SliderWidget(10, 10, 100, 20, Text.literal("Red: " + convertTo255( handler.getRed() )).styled(style -> style.withColor(TextColor.fromRgb(0xFF0000))), handler.getRed()) {
        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("Red: " + convertTo255( handler.getRed() )).styled(style -> style.withColor(TextColor.fromRgb(0xFF0000))));
        }

        @Override
        protected void applyValue() {
            handler.updateZoneColor((float)this.value, "red");
        }
    };

    public SliderWidget greenSlider = new SliderWidget(10, 40, 100, 20, Text.literal("Green: " + convertTo255( handler.getGreen() )).styled(style -> style.withColor(TextColor.fromRgb(0x00FF00))), handler.getGreen()) {
        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("Green: " + convertTo255( handler.getGreen() )).styled(style -> style.withColor(TextColor.fromRgb(0x00FF00))));
        }

        @Override
        protected void applyValue() {
            handler.updateZoneColor((float)this.value, "green");
        }
    };

    public SliderWidget blueSlider = new SliderWidget(10, 70, 100, 20, Text.literal("Blue: " + convertTo255( handler.getBlue() )).styled(style -> style.withColor(TextColor.fromRgb(0x0000FF))), handler.getBlue()) {
        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("Blue: " + convertTo255( handler.getBlue() )).styled(style -> style.withColor(TextColor.fromRgb(0x0000FF))));
        }

        @Override
        protected void applyValue() {
            handler.updateZoneColor((float)this.value, "blue");
        }
    };



    @Override
    protected void init() {
        super.init();

        saveMapName.setPosition(width/2 + 45, 50);
        addDrawableChild(saveMapName);

        addDrawableChild(redSlider);
        addDrawableChild(greenSlider);
        addDrawableChild(blueSlider);

        // Supprimez les composants de texte par défaut
        this.titleX = Integer.MIN_VALUE;
        this.titleY = Integer.MIN_VALUE;
        this.playerInventoryTitleX = Integer.MIN_VALUE;
        this.playerInventoryTitleY = Integer.MIN_VALUE;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("ZONE"), width / 2, height / 2, 0xffffff);
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
}
