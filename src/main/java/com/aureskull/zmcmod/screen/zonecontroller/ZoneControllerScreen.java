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
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class ZoneControllerScreen extends HandledScreen<ZoneControllerScreenHandler> {
    public final int PADDING = 4;
    public final int RIGHT_ELEMENT_MAX_SIZE = 110;
    public final int RIGHT_PADDING = 10;
    public final int TOP_PADDING = 10;
    public final int SECTION_PADDING_TEXT = 10;
    public final int SECTION_PADDING = 22;
    public final int SLIDER_WIDTH = RIGHT_ELEMENT_MAX_SIZE;
    public final int TEXTFIELD_WIDTH = 50;
    public final int TEXTFIELD_LEFT_PADDING = 15;
    public final int LEFT_TEXT_BLOCKPOS_OFFSET = 7;
    public final int TOP_TEXT_BLOCKPOS_OFFSET = 6;
    public final int LEFT_OFFSET_POS_BUTTON = 14;
    public final int INC_DEC_SIZE_POS_BUTTON = 10;

    public ZoneControllerScreen(ZoneControllerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }



    public SliderWidget redSlider = new SliderWidget(0, 0, SLIDER_WIDTH, 20, Text.literal("Red: " + convertTo255( handler.zoneControllerBlockEntity.getRed() )).styled(style -> style.withColor(TextColor.fromRgb(0xFF0000))), handler.zoneControllerBlockEntity.getRed()) {
        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("Red: " + convertTo255( handler.zoneControllerBlockEntity.getRed() )).styled(style -> style.withColor(TextColor.fromRgb(0xFF0000))));
        }

        @Override
        protected void applyValue() {
            handler.updateZoneColor((float)this.value, "red");
        }
    };

    public SliderWidget greenSlider = new SliderWidget(0, 0, SLIDER_WIDTH, 20, Text.literal("Green: " + convertTo255( handler.zoneControllerBlockEntity.getGreen() )).styled(style -> style.withColor(TextColor.fromRgb(0x00FF00))), handler.zoneControllerBlockEntity.getGreen()) {
        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("Green: " + convertTo255( handler.zoneControllerBlockEntity.getGreen() )).styled(style -> style.withColor(TextColor.fromRgb(0x00FF00))));
        }

        @Override
        protected void applyValue() {
            handler.updateZoneColor((float)this.value, "green");
        }
    };

    public SliderWidget blueSlider = new SliderWidget(0, 0, SLIDER_WIDTH, 20, Text.literal("Blue: " + convertTo255( handler.zoneControllerBlockEntity.getBlue() )).styled(style -> style.withColor(TextColor.fromRgb(0x0000FF))), handler.zoneControllerBlockEntity.getBlue()) {
        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("Blue: " + convertTo255( handler.zoneControllerBlockEntity.getBlue() )).styled(style -> style.withColor(TextColor.fromRgb(0x0000FF))));
        }

        @Override
        protected void applyValue() {
            handler.updateZoneColor((float)this.value, "blue");
        }
    };

    //POSITION A
    private TextFieldWidget posAXTextField;

    public ButtonWidget incrementPosAXButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posA = this.handler.zoneControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX() + 1, posA.getY(), posA.getZ());
                this.handler.updatePos(newPosA, "posA");

                posAXTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getX()));
            })
            .dimensions(0, 0, 10, 10)
            .build();

    public ButtonWidget decrementPosAXButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posA = this.handler.zoneControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX() - 1, posA.getY(), posA.getZ());
                this.handler.updatePos(newPosA, "posA");

                posAXTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getX()));
            })
            .dimensions(0, 0, 10, 10)
            .build();

    private TextFieldWidget posAYTextField;

    public ButtonWidget incrementPosAYButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posA = this.handler.zoneControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX(), posA.getY() + 1, posA.getZ());
                this.handler.updatePos(newPosA, "posA");

                posAYTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getY()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();

    public ButtonWidget decrementPosAYButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posA = this.handler.zoneControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX(), posA.getY() - 1, posA.getZ());
                this.handler.updatePos(newPosA, "posA");

                posAYTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getY()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();

    private TextFieldWidget posAZTextField;

    public ButtonWidget incrementPosAZButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posA = this.handler.zoneControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX(), posA.getY(), posA.getZ() + 1);
                this.handler.updatePos(newPosA, "posA");

                posAZTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getZ()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();

    public ButtonWidget decrementPosAZButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posA = this.handler.zoneControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX(), posA.getY(), posA.getZ() - 1);
                this.handler.updatePos(newPosA, "posA");

                posAZTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getZ()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();



    //POSITION B
    private TextFieldWidget posBXTextField;

    public ButtonWidget incrementPosBXButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posB = this.handler.zoneControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX() + 1, posB.getY(), posB.getZ());
                this.handler.updatePos(newPosB, "posB");

                posBXTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getX()));
            })
            .dimensions(0, 0, 10, 10)
            .build();

    public ButtonWidget decrementPosBXButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posB = this.handler.zoneControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX() - 1, posB.getY(), posB.getZ());
                this.handler.updatePos(newPosB, "posB");

                posBXTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getX()));
            })
            .dimensions(0, 0, 10, 10)
            .build();

    private TextFieldWidget posBYTextField;

    public ButtonWidget incrementPosBYButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posB = this.handler.zoneControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX(), posB.getY() + 1, posB.getZ());
                this.handler.updatePos(newPosB, "posB");

                posBYTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getY()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();

    public ButtonWidget decrementPosBYButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posB = this.handler.zoneControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX(), posB.getY() - 1, posB.getZ());
                this.handler.updatePos(newPosB, "posB");

                posBYTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getY()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();

    private TextFieldWidget posBZTextField;

    public ButtonWidget incrementPosBZButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posB = this.handler.zoneControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX(), posB.getY(), posB.getZ() + 1);
                this.handler.updatePos(newPosB, "posB");

                posBZTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getZ()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();

    public ButtonWidget decrementPosBZButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posB = this.handler.zoneControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX(), posB.getY(), posB.getZ() - 1);
                this.handler.updatePos(newPosB, "posB");

                posBZTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getZ()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();


    //SPAWN POSITION
    private TextFieldWidget spawnPosXTextField;

    public ButtonWidget incrementSpawnPosXButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos spawnPoint = this.handler.zoneControllerBlockEntity.getSpawnPoint();

                BlockPos newSpawnPoint = new BlockPos(spawnPoint.getX() + 1, spawnPoint.getY(), spawnPoint.getZ());
                this.handler.updatePos(newSpawnPoint, "spawnPos");

                spawnPosXTextField.setText(String.valueOf(newSpawnPoint.getX()));
            })
            .dimensions(0, 0, 10, 10)
            .build();

    public ButtonWidget decrementSpawnPosXButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos spawnPoint = this.handler.zoneControllerBlockEntity.getSpawnPoint();

                BlockPos newSpawnPoint = new BlockPos(spawnPoint.getX() - 1, spawnPoint.getY(), spawnPoint.getZ());
                this.handler.updatePos(newSpawnPoint, "spawnPos");

                spawnPosXTextField.setText(String.valueOf(newSpawnPoint.getX()));
            })
            .dimensions(0, 0, 10, 10)
            .build();

    private TextFieldWidget spawnPosYTextField;

    public ButtonWidget incrementSpawnPosYButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos spawnPoint = this.handler.zoneControllerBlockEntity.getSpawnPoint();

                BlockPos newSpawnPoint = new BlockPos(spawnPoint.getX(), spawnPoint.getY() + 1, spawnPoint.getZ());
                this.handler.updatePos(newSpawnPoint, "spawnPos");

                spawnPosYTextField.setText(String.valueOf(newSpawnPoint.getY()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();

    public ButtonWidget decrementSpawnPosYButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos spawnPoint = this.handler.zoneControllerBlockEntity.getSpawnPoint();

                BlockPos newSpawnPoint = new BlockPos(spawnPoint.getX(), spawnPoint.getY() - 1, spawnPoint.getZ());
                this.handler.updatePos(newSpawnPoint, "spawnPos");

                spawnPosYTextField.setText(String.valueOf(newSpawnPoint.getY()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();

    private TextFieldWidget spawnPosZTextField;

    public ButtonWidget incrementSpawnPosZButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos spawnPoint = this.handler.zoneControllerBlockEntity.getSpawnPoint();

                BlockPos newSpawnPoint = new BlockPos(spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ() + 1);
                this.handler.updatePos(newSpawnPoint, "spawnPos");

                spawnPosZTextField.setText(String.valueOf(newSpawnPoint.getZ()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();

    public ButtonWidget decrementSpawnPosZButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos spawnPoint = this.handler.zoneControllerBlockEntity.getSpawnPoint();

                BlockPos newSpawnPoint = new BlockPos(spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ() - 1);
                this.handler.updatePos(newSpawnPoint, "spawnPos");

                spawnPosZTextField.setText(String.valueOf(newSpawnPoint.getZ()));
            })
            .dimensions(0, 0, INC_DEC_SIZE_POS_BUTTON, INC_DEC_SIZE_POS_BUTTON)
            .build();


    @Override
    protected void init() {
        super.init();
        createElements();
        addElements();

        posAXTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getX()));
        posAYTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getY()));
        posAZTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getZ()));

        posBXTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getX()));
        posBYTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getY()));
        posBZTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getZ()));

        spawnPosXTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getSpawnPoint().getX()));
        spawnPosYTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getSpawnPoint().getY()));
        spawnPosZTextField.setText(String.valueOf(this.handler.zoneControllerBlockEntity.getSpawnPoint().getZ()));

        redSlider.setPosition(width - redSlider.getWidth() - RIGHT_PADDING, 23);
        greenSlider.setPosition(width - redSlider.getWidth() - RIGHT_PADDING, redSlider.getY() + redSlider.getHeight() + PADDING);
        blueSlider.setPosition(width - redSlider.getWidth() - RIGHT_PADDING, greenSlider.getY() + greenSlider.getHeight() + PADDING);

        //POSA
        posAXTextField.setPosition(width - TEXTFIELD_WIDTH - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + TEXTFIELD_LEFT_PADDING, blueSlider.getHeight() + blueSlider.getY() + SECTION_PADDING + 1);
        incrementPosAXButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2 , blueSlider.getHeight() + blueSlider.getY() + SECTION_PADDING + 1);
        decrementPosAXButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2, incrementPosAXButtonWidget.getY() + incrementPosAXButtonWidget.getHeight() - 2);

        posAYTextField.setPosition(width - TEXTFIELD_WIDTH - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + TEXTFIELD_LEFT_PADDING, posAXTextField.getHeight() + posAXTextField.getY() + 1);
        incrementPosAYButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2 , posAXTextField.getHeight() + posAXTextField.getY() + 1);
        decrementPosAYButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2, incrementPosAYButtonWidget.getY() + incrementPosAYButtonWidget.getHeight() - 2);

        posAZTextField.setPosition(width - TEXTFIELD_WIDTH - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + TEXTFIELD_LEFT_PADDING, posAYTextField.getHeight() + posAYTextField.getY() + 1);
        incrementPosAZButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2 , posAYTextField.getHeight() + posAYTextField.getY() + 1);
        decrementPosAZButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2, incrementPosAZButtonWidget.getY() + incrementPosAZButtonWidget.getHeight() - 2);

        //POSB
        posBXTextField.setPosition(width - TEXTFIELD_WIDTH - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + TEXTFIELD_LEFT_PADDING, posAZTextField.getHeight() + posAZTextField.getY() + SECTION_PADDING + 1);
        incrementPosBXButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2 , posAZTextField.getHeight() + posAZTextField.getY() + SECTION_PADDING + 1);
        decrementPosBXButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2, incrementPosBXButtonWidget.getY() + incrementPosBXButtonWidget.getHeight() - 2);

        posBYTextField.setPosition(width - TEXTFIELD_WIDTH - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + TEXTFIELD_LEFT_PADDING, posBXTextField.getHeight() + posBXTextField.getY() + 1);
        incrementPosBYButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2 , posBXTextField.getHeight() + posBXTextField.getY() + 1);
        decrementPosBYButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2, incrementPosBYButtonWidget.getY() + incrementPosBYButtonWidget.getHeight() - 2);

        posBZTextField.setPosition(width - TEXTFIELD_WIDTH - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + TEXTFIELD_LEFT_PADDING, posBYTextField.getHeight() + posBYTextField.getY() + 1);
        incrementPosBZButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2 , posBYTextField.getHeight() + posBYTextField.getY() + 1);
        decrementPosBZButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2, incrementPosBZButtonWidget.getY() + incrementPosBZButtonWidget.getHeight() - 2);

        //SPAWN POSITION
        spawnPosXTextField.setPosition(width - TEXTFIELD_WIDTH - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + TEXTFIELD_LEFT_PADDING, posBZTextField.getHeight() + posBZTextField.getY() + SECTION_PADDING + 1);
        incrementSpawnPosXButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2 , posBZTextField.getHeight() + posBZTextField.getY() + SECTION_PADDING + 1);
        decrementSpawnPosXButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2, incrementSpawnPosXButtonWidget.getY() + incrementSpawnPosXButtonWidget.getHeight() - 2);

        spawnPosYTextField.setPosition(width - TEXTFIELD_WIDTH - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + TEXTFIELD_LEFT_PADDING, spawnPosXTextField.getHeight() + spawnPosXTextField.getY() + 1);
        incrementSpawnPosYButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2 , spawnPosXTextField.getHeight() + spawnPosXTextField.getY() + 1);
        decrementSpawnPosYButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2, incrementSpawnPosYButtonWidget.getY() + incrementSpawnPosYButtonWidget.getHeight() - 2);

        spawnPosZTextField.setPosition(width - TEXTFIELD_WIDTH - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + TEXTFIELD_LEFT_PADDING, spawnPosYTextField.getHeight() + spawnPosYTextField.getY() + 1);
        incrementSpawnPosZButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2 , spawnPosYTextField.getHeight() + spawnPosYTextField.getY() + 1);
        decrementSpawnPosZButtonWidget.setPosition(width - RIGHT_PADDING - (RIGHT_ELEMENT_MAX_SIZE - TEXTFIELD_WIDTH) + LEFT_OFFSET_POS_BUTTON + 2, incrementSpawnPosZButtonWidget.getY() + incrementSpawnPosZButtonWidget.getHeight() - 2);

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
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Y:"), width - RIGHT_PADDING - RIGHT_ELEMENT_MAX_SIZE + LEFT_TEXT_BLOCKPOS_OFFSET, posAXTextField.getHeight() + posAXTextField.getY() + TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Z:"), width - RIGHT_PADDING - RIGHT_ELEMENT_MAX_SIZE + LEFT_TEXT_BLOCKPOS_OFFSET, posAYTextField.getHeight() + posAYTextField.getY() + TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Second zone position"), width - 57 - RIGHT_PADDING, posAZTextField.getY() + posAZTextField.getHeight() + SECTION_PADDING_TEXT, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("X:"), width - RIGHT_PADDING - RIGHT_ELEMENT_MAX_SIZE + LEFT_TEXT_BLOCKPOS_OFFSET, posAZTextField.getHeight() + posAZTextField.getY() + SECTION_PADDING + TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Y:"), width - RIGHT_PADDING - RIGHT_ELEMENT_MAX_SIZE + LEFT_TEXT_BLOCKPOS_OFFSET, posBXTextField.getHeight() + posBXTextField.getY() + TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Z:"), width - RIGHT_PADDING - RIGHT_ELEMENT_MAX_SIZE + LEFT_TEXT_BLOCKPOS_OFFSET, posBYTextField.getHeight() + posBYTextField.getY() + TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Player spawn location"), width - 55 - RIGHT_PADDING, posBZTextField.getY() + posBZTextField.getHeight() + SECTION_PADDING_TEXT, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("X:"), width - RIGHT_PADDING - RIGHT_ELEMENT_MAX_SIZE + LEFT_TEXT_BLOCKPOS_OFFSET, posBZTextField.getHeight() + posBZTextField.getY() + SECTION_PADDING + TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Y:"), width - RIGHT_PADDING - RIGHT_ELEMENT_MAX_SIZE + LEFT_TEXT_BLOCKPOS_OFFSET, spawnPosXTextField.getHeight() + spawnPosXTextField.getY() + TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Z:"), width - RIGHT_PADDING - RIGHT_ELEMENT_MAX_SIZE + LEFT_TEXT_BLOCKPOS_OFFSET, spawnPosYTextField.getHeight() + spawnPosYTextField.getY() + TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
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
        posAXTextField = new TextFieldWidget(this.textRenderer, 0,  0, TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getX())));
        posAYTextField = new TextFieldWidget(this.textRenderer, 0,  0, TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getY())));
        posAZTextField = new TextFieldWidget(this.textRenderer, 0,  0, TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.zoneControllerBlockEntity.getPosA().getZ())));

        posBXTextField = new TextFieldWidget(this.textRenderer, 0,  0, TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getX())));
        posBYTextField = new TextFieldWidget(this.textRenderer, 0,  0, TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getY())));
        posBZTextField = new TextFieldWidget(this.textRenderer, 0,  0, TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.zoneControllerBlockEntity.getPosB().getZ())));

        spawnPosXTextField = new TextFieldWidget(this.textRenderer, 0,  0, TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.zoneControllerBlockEntity.getSpawnPoint().getX())));
        spawnPosYTextField = new TextFieldWidget(this.textRenderer, 0,  0, TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.zoneControllerBlockEntity.getSpawnPoint().getY())));
        spawnPosZTextField = new TextFieldWidget(this.textRenderer, 0,  0, TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.zoneControllerBlockEntity.getSpawnPoint().getZ())));
    }

    private void addElements(){
        addDrawableChild(redSlider);
        addDrawableChild(greenSlider);
        addDrawableChild(blueSlider);

        //POSA
        addDrawableChild(posAXTextField);
        addDrawableChild(incrementPosAXButtonWidget);
        addDrawableChild(decrementPosAXButtonWidget);

        addDrawableChild(posAYTextField);
        addDrawableChild(incrementPosAYButtonWidget);
        addDrawableChild(decrementPosAYButtonWidget);

        addDrawableChild(posAZTextField);
        addDrawableChild(incrementPosAZButtonWidget);
        addDrawableChild(decrementPosAZButtonWidget);

        //POSB
        addDrawableChild(posBXTextField);
        addDrawableChild(incrementPosBXButtonWidget);
        addDrawableChild(decrementPosBXButtonWidget);

        addDrawableChild(posBYTextField);
        addDrawableChild(incrementPosBYButtonWidget);
        addDrawableChild(decrementPosBYButtonWidget);

        addDrawableChild(posBZTextField);
        addDrawableChild(incrementPosBZButtonWidget);
        addDrawableChild(decrementPosBZButtonWidget);

        //SPAWN POSITION
        addDrawableChild(spawnPosXTextField);
        addDrawableChild(incrementSpawnPosXButtonWidget);
        addDrawableChild(decrementSpawnPosXButtonWidget);

        addDrawableChild(spawnPosYTextField);
        addDrawableChild(incrementSpawnPosYButtonWidget);
        addDrawableChild(decrementSpawnPosYButtonWidget);

        addDrawableChild(spawnPosZTextField);
        addDrawableChild(incrementSpawnPosZButtonWidget);
        addDrawableChild(decrementSpawnPosZButtonWidget);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        updatePositionOnFocusLost(posAXTextField, "AX");
        updatePositionOnFocusLost(posAYTextField, "AY");
        updatePositionOnFocusLost(posAZTextField, "AZ");

        updatePositionOnFocusLost(posBXTextField, "BX");
        updatePositionOnFocusLost(posBYTextField, "BY");
        updatePositionOnFocusLost(posBZTextField, "BZ");

        updatePositionOnFocusLost(spawnPosXTextField, "CX");
        updatePositionOnFocusLost(spawnPosYTextField, "CY");
        updatePositionOnFocusLost(spawnPosZTextField, "CZ");
        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean handled = super.keyPressed(keyCode, scanCode, modifiers);
        if (handled) {
            updatePositionOnFocusLost(posAXTextField, "AX");
            updatePositionOnFocusLost(posAYTextField, "AY");
            updatePositionOnFocusLost(posAZTextField, "AZ");

            updatePositionOnFocusLost(posBXTextField, "BX");
            updatePositionOnFocusLost(posBYTextField, "BY");
            updatePositionOnFocusLost(posBZTextField, "BZ");

            updatePositionOnFocusLost(spawnPosXTextField, "CX");
            updatePositionOnFocusLost(spawnPosYTextField, "CY");
            updatePositionOnFocusLost(spawnPosZTextField, "CZ");
        }
        return handled;
    }

    private void updatePositionOnFocusLost(TextFieldWidget textField, String axis) {
        try {
            int newValue = Integer.parseInt(textField.getText());
            BlockPos currentPos = null;

            if(axis.startsWith("A")){
                currentPos = handler.zoneControllerBlockEntity.getPosA();
            }else if(axis.startsWith("B")){
                currentPos = handler.zoneControllerBlockEntity.getPosB();
            }else if(axis.startsWith("C")){
                currentPos = handler.zoneControllerBlockEntity.getSpawnPoint();
            }

            BlockPos newPos = switch (axis) {
                case "AX" -> new BlockPos(newValue, currentPos.getY(), currentPos.getZ());
                case "AY" -> new BlockPos(currentPos.getX(), newValue, currentPos.getZ());
                case "AZ" -> new BlockPos(currentPos.getX(), currentPos.getY(), newValue);
                case "BX" -> new BlockPos(newValue, currentPos.getY(), currentPos.getZ());
                case "BY" -> new BlockPos(currentPos.getX(), newValue, currentPos.getZ());
                case "BZ" -> new BlockPos(currentPos.getX(), currentPos.getY(), newValue);
                case "CX" -> new BlockPos(newValue, currentPos.getY(), currentPos.getZ());
                case "CY" -> new BlockPos(currentPos.getX(), newValue, currentPos.getZ());
                case "CZ" -> new BlockPos(currentPos.getX(), currentPos.getY(), newValue);
                default -> currentPos;
            };

            if (axis.startsWith("A")) {
                handler.zoneControllerBlockEntity.setPosA(newPos);
                handler.updatePos(newPos, "posA");
            } else if(axis.startsWith("B")){
                handler.zoneControllerBlockEntity.setPosB(newPos);
                handler.updatePos(newPos, "posB");
            } else if(axis.startsWith("C")){
                handler.zoneControllerBlockEntity.setSpawnPoint(newPos);
                handler.updatePos(newPos, "spawnPos");
            }

            textField.setText(String.valueOf(newValue)); // Update the text field with the new value
        } catch (NumberFormatException e) {
            // Reset to the last valid value if the new text is not an integer
            if (axis.charAt(0) == 'A') {
                switch (axis.charAt(1)) {
                    case 'X' -> textField.setText(String.valueOf(handler.zoneControllerBlockEntity.getPosA().getX()));
                    case 'Y' -> textField.setText(String.valueOf(handler.zoneControllerBlockEntity.getPosA().getY()));
                    case 'Z' -> textField.setText(String.valueOf(handler.zoneControllerBlockEntity.getPosA().getZ()));
                }
            } else if(axis.charAt(0) == 'B') {
                switch (axis.charAt(1)) {
                    case 'X' -> textField.setText(String.valueOf(handler.zoneControllerBlockEntity.getPosB().getX()));
                    case 'Y' -> textField.setText(String.valueOf(handler.zoneControllerBlockEntity.getPosB().getY()));
                    case 'Z' -> textField.setText(String.valueOf(handler.zoneControllerBlockEntity.getPosB().getZ()));
                }
            } else if(axis.charAt(0) == 'C') {
                switch (axis.charAt(1)) {
                    case 'X' -> textField.setText(String.valueOf(handler.zoneControllerBlockEntity.getSpawnPoint().getX()));
                    case 'Y' -> textField.setText(String.valueOf(handler.zoneControllerBlockEntity.getSpawnPoint().getY()));
                    case 'Z' -> textField.setText(String.valueOf(handler.zoneControllerBlockEntity.getSpawnPoint().getZ()));
                }
            }
        }
    }
}
