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
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class MapControllerScreen extends HandledScreen<MapControllerScreenHandler> {
    private TextFieldWidget mapNameTextField;
    private ButtonWidget startGameButton = ButtonWidget.builder(Text.literal("")
            .append(Text.literal(this.handler.mapControllerBlockEntity.isStarted() ? "STARTED" : "STOPPED")
            .formatted(this.handler.mapControllerBlockEntity.isStarted() ? Formatting.GREEN : Formatting.RED)), button -> {

                Boolean newState = !this.handler.mapControllerBlockEntity.isStarted();
                this.handler.mapControllerBlockEntity.setStart(newState, null);
                this.handler.updateStartGameOnServer(newState);

                this.startGameButton.setMessage(Text.literal("")
                        .append(Text.literal(this.handler.mapControllerBlockEntity.isStarted() ? "STARTED" : "STOPPED")
                                .formatted(this.handler.mapControllerBlockEntity.isStarted() ? Formatting.GREEN : Formatting.RED)));

            })
            .dimensions(0, 0, 65, ScreenConstants.BUTTON_HEIGHT)
            .build();


    public ButtonWidget saveMapNameButton = ButtonWidget.builder(Text.literal("Save"), button -> {
            this.handler.updateMapName(mapNameTextField.getText());
        })
        .dimensions(0, 0, 45, ScreenConstants.BUTTON_HEIGHT)
        .build();


    //POSITION A
    private TextFieldWidget posAXTextField;

    public ButtonWidget incrementPosAXButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posA = this.handler.mapControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX() + 1, posA.getY(), posA.getZ());
                this.handler.updatePos(newPosA, "posA");

                posAXTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getX()));
            })
            .dimensions(0, 0, 10, 10)
            .build();

    public ButtonWidget decrementPosAXButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posA = this.handler.mapControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX() - 1, posA.getY(), posA.getZ());
                this.handler.updatePos(newPosA, "posA");

                posAXTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getX()));
            })
            .dimensions(0, 0, 10, 10)
            .build();

    private TextFieldWidget posAYTextField;

    public ButtonWidget incrementPosAYButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posA = this.handler.mapControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX(), posA.getY() + 1, posA.getZ());
                this.handler.updatePos(newPosA, "posA");

                posAYTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getY()));
            })
            .dimensions(0, 0, ScreenConstants.INC_DEC_SIZE_POS_BUTTON, ScreenConstants.INC_DEC_SIZE_POS_BUTTON)
            .build();

    public ButtonWidget decrementPosAYButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posA = this.handler.mapControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX(), posA.getY() - 1, posA.getZ());
                this.handler.updatePos(newPosA, "posA");

                posAYTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getY()));
            })
            .dimensions(0, 0, ScreenConstants.INC_DEC_SIZE_POS_BUTTON, ScreenConstants.INC_DEC_SIZE_POS_BUTTON)
            .build();

    private TextFieldWidget posAZTextField;

    public ButtonWidget incrementPosAZButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posA = this.handler.mapControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX(), posA.getY(), posA.getZ() + 1);
                this.handler.updatePos(newPosA, "posA");

                posAZTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getZ()));
            })
            .dimensions(0, 0, ScreenConstants.INC_DEC_SIZE_POS_BUTTON, ScreenConstants.INC_DEC_SIZE_POS_BUTTON)
            .build();

    public ButtonWidget decrementPosAZButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posA = this.handler.mapControllerBlockEntity.getPosA();

                BlockPos newPosA = new BlockPos(posA.getX(), posA.getY(), posA.getZ() - 1);
                this.handler.updatePos(newPosA, "posA");

                posAZTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getZ()));
            })
            .dimensions(0, 0, ScreenConstants.INC_DEC_SIZE_POS_BUTTON, ScreenConstants.INC_DEC_SIZE_POS_BUTTON)
            .build();



    //POSITION B
    private TextFieldWidget posBXTextField;

    public ButtonWidget incrementPosBXButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posB = this.handler.mapControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX() + 1, posB.getY(), posB.getZ());
                this.handler.updatePos(newPosB, "posB");

                posBXTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getX()));
            })
            .dimensions(0, 0, 10, 10)
            .build();

    public ButtonWidget decrementPosBXButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posB = this.handler.mapControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX() - 1, posB.getY(), posB.getZ());
                this.handler.updatePos(newPosB, "posB");

                posBXTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getX()));
            })
            .dimensions(0, 0, 10, 10)
            .build();

    private TextFieldWidget posBYTextField;

    public ButtonWidget incrementPosBYButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posB = this.handler.mapControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX(), posB.getY() + 1, posB.getZ());
                this.handler.updatePos(newPosB, "posB");

                posBYTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getY()));
            })
            .dimensions(0, 0, ScreenConstants.INC_DEC_SIZE_POS_BUTTON, ScreenConstants.INC_DEC_SIZE_POS_BUTTON)
            .build();

    public ButtonWidget decrementPosBYButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posB = this.handler.mapControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX(), posB.getY() - 1, posB.getZ());
                this.handler.updatePos(newPosB, "posB");

                posBYTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getY()));
            })
            .dimensions(0, 0, ScreenConstants.INC_DEC_SIZE_POS_BUTTON, ScreenConstants.INC_DEC_SIZE_POS_BUTTON)
            .build();

    private TextFieldWidget posBZTextField;

    public ButtonWidget incrementPosBZButtonWidget = ButtonWidget.builder(Text.literal("˄"), button -> {
                BlockPos posB = this.handler.mapControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX(), posB.getY(), posB.getZ() + 1);
                this.handler.updatePos(newPosB, "posB");

                posBZTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getZ()));
            })
            .dimensions(0, 0, ScreenConstants.INC_DEC_SIZE_POS_BUTTON, ScreenConstants.INC_DEC_SIZE_POS_BUTTON)
            .build();

    public ButtonWidget decrementPosBZButtonWidget = ButtonWidget.builder(Text.literal("˅"), button -> {
                BlockPos posB = this.handler.mapControllerBlockEntity.getPosB();

                BlockPos newPosB = new BlockPos(posB.getX(), posB.getY(), posB.getZ() - 1);
                this.handler.updatePos(newPosB, "posB");

                posBZTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getZ()));
            })
            .dimensions(0, 0, ScreenConstants.INC_DEC_SIZE_POS_BUTTON, ScreenConstants.INC_DEC_SIZE_POS_BUTTON)
            .build();

    public MapControllerScreen(MapControllerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        createElements();
        addElements();

        mapNameTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getMapName()));

        mapNameTextField.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET, 23);
        saveMapNameButton.setPosition(width - saveMapNameButton.getWidth() - ScreenConstants.RIGHT_PADDING, 23);

        startGameButton.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET, saveMapNameButton.getY() + saveMapNameButton.getHeight() + ScreenConstants.PADDING);



        posAXTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getX()));
        posAYTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getY()));
        posAZTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getZ()));

        posBXTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getX()));
        posBYTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getY()));
        posBZTextField.setText(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getZ()));

        //POSA
        posAXTextField.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET, startGameButton.getHeight() + startGameButton.getY() + ScreenConstants.SECTION_PADDING + 1);
        incrementPosAXButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posAXTextField.getWidth(), startGameButton.getHeight() + startGameButton.getY() + ScreenConstants.SECTION_PADDING + 1);
        decrementPosAXButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posAXTextField.getWidth(), incrementPosAXButtonWidget.getY() + incrementPosAXButtonWidget.getHeight() - 2);

        posAYTextField.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET, posAXTextField.getHeight() + posAXTextField.getY() + 1);
        incrementPosAYButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posAYTextField.getWidth(), posAXTextField.getHeight() + posAXTextField.getY() + 1);
        decrementPosAYButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posAYTextField.getWidth(), incrementPosAYButtonWidget.getY() + incrementPosAYButtonWidget.getHeight() - 2);

        posAZTextField.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET, posAYTextField.getHeight() + posAYTextField.getY() + 1);
        incrementPosAZButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posAZTextField.getWidth(), posAYTextField.getHeight() + posAYTextField.getY() + 1);
        decrementPosAZButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posAZTextField.getWidth(), incrementPosAZButtonWidget.getY() + incrementPosAZButtonWidget.getHeight() - 2);

        //POSB
        posBXTextField.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET, posAZTextField.getHeight() + posAZTextField.getY() + ScreenConstants.SECTION_PADDING + 1);
        incrementPosBXButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posBXTextField.getWidth(), posAZTextField.getHeight() + posAZTextField.getY() + ScreenConstants.SECTION_PADDING + 1);
        decrementPosBXButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posBXTextField.getWidth(), incrementPosBXButtonWidget.getY() + incrementPosBXButtonWidget.getHeight() - 2);

        posBYTextField.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET, posBXTextField.getHeight() + posBXTextField.getY() + 1);
        incrementPosBYButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posBYTextField.getWidth(), posBXTextField.getHeight() + posBXTextField.getY() + 1);
        decrementPosBYButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posBYTextField.getWidth(), incrementPosBYButtonWidget.getY() + incrementPosBYButtonWidget.getHeight() - 2);

        posBZTextField.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET, posBYTextField.getHeight() + posBYTextField.getY() + 1);
        incrementPosBZButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posBZTextField.getWidth(), posBYTextField.getHeight() + posBYTextField.getY() + 1);
        decrementPosBZButtonWidget.setPosition(width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET + posBZTextField.getWidth(), incrementPosBZButtonWidget.getY() + incrementPosBZButtonWidget.getHeight() - 2);


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
    }

    private void createElements(){
        this.mapNameTextField = new TextFieldWidget(this.textRenderer, 0,  0, ScreenConstants.TEXTFIELD_WIDTH, ScreenConstants.TEXTFIELD_HEIGHT, Text.of(this.handler.mapControllerBlockEntity.getMapName()));

        posAXTextField = new TextFieldWidget(this.textRenderer, 0,  0, ScreenConstants.TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getX())));
        posAYTextField = new TextFieldWidget(this.textRenderer, 0,  0, ScreenConstants.TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getY())));
        posAZTextField = new TextFieldWidget(this.textRenderer, 0,  0, ScreenConstants.TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.mapControllerBlockEntity.getPosA().getZ())));

        posBXTextField = new TextFieldWidget(this.textRenderer, 0,  0, ScreenConstants.TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getX())));
        posBYTextField = new TextFieldWidget(this.textRenderer, 0,  0, ScreenConstants.TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getY())));
        posBZTextField = new TextFieldWidget(this.textRenderer, 0,  0, ScreenConstants.TEXTFIELD_WIDTH, 18, Text.of(String.valueOf(this.handler.mapControllerBlockEntity.getPosB().getZ())));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta); //Ne pas afficher l'inventaire etc
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Map: " + mapNameTextField.getText()), 10 + textRenderer.getWidth(Text.literal("Map: " + mapNameTextField.getText()))/2, 10, 0xffffff);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Map Information"), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE - ScreenConstants.RIGHT_PADDING + (textRenderer.getWidth(Text.literal("Map Information"))/2), ScreenConstants.TOP_PADDING, 0xffffff);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Name: "), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE - ScreenConstants.RIGHT_PADDING + (textRenderer.getWidth(Text.literal("Name: "))/2) + ScreenConstants.SECTION_PADDING_TEXT, ScreenConstants.SECTION_PADDING + ScreenConstants.TOP_TEXT_OFFSET_SECTION, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("State: "), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE - ScreenConstants.RIGHT_PADDING + (textRenderer.getWidth(Text.literal("State: "))/2) + ScreenConstants.SECTION_PADDING_TEXT, mapNameTextField.getHeight() + mapNameTextField.getY() + ScreenConstants.TOP_TEXT_OFFSET, 0xffffff);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("First zone position"), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE - ScreenConstants.RIGHT_PADDING + (textRenderer.getWidth(Text.literal("First zone position"))/2), startGameButton.getY() + startGameButton.getHeight() + ScreenConstants.SECTION_PADDING_TEXT, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("X:"), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET - textRenderer.getWidth(Text.literal("X:")), startGameButton.getHeight() + startGameButton.getY() + ScreenConstants.SECTION_PADDING + ScreenConstants.TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Y:"), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET - textRenderer.getWidth(Text.literal("Y:")), posAXTextField.getHeight() + posAXTextField.getY() + ScreenConstants.TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Z:"), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET - textRenderer.getWidth(Text.literal("Z:")), posAYTextField.getHeight() + posAYTextField.getY() + ScreenConstants.TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Second zone position"), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE - ScreenConstants.RIGHT_PADDING + (textRenderer.getWidth(Text.literal("Second zone position"))/2), posAZTextField.getY() + posAZTextField.getHeight() + ScreenConstants.SECTION_PADDING_TEXT, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("X:"), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET - textRenderer.getWidth(Text.literal("X:")), posAZTextField.getHeight() + posAZTextField.getY() + ScreenConstants.SECTION_PADDING + ScreenConstants.TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Y:"), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET - textRenderer.getWidth(Text.literal("Y:")), posBXTextField.getHeight() + posBXTextField.getY() + ScreenConstants.TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Z:"), width - ScreenConstants.RIGHT_ELEMENT_MAX_SIZE + ScreenConstants.INLINE_ELEMENT_LEFT_OFFSET - textRenderer.getWidth(Text.literal("Z:")), posBYTextField.getHeight() + posBYTextField.getY() + ScreenConstants.TOP_TEXT_BLOCKPOS_OFFSET, 0xffffff);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean handled = super.keyPressed(keyCode, scanCode, modifiers);

        if (handled) {
            if (keyCode == InputUtil.GLFW_KEY_E) {
                return true;
            }

            updatePositionOnFocusLost(posAXTextField, "AX");
            updatePositionOnFocusLost(posAYTextField, "AY");
            updatePositionOnFocusLost(posAZTextField, "AZ");
            updatePositionOnFocusLost(posBXTextField, "BX");
            updatePositionOnFocusLost(posBYTextField, "BY");
            updatePositionOnFocusLost(posBZTextField, "BZ");
        }

        return handled;
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
        return result;
    }

    private void updatePositionOnFocusLost(TextFieldWidget textField, String axis) {
        try {
            int newValue = Integer.parseInt(textField.getText());
            BlockPos currentPos = axis.startsWith("A") ? handler.mapControllerBlockEntity.getPosA() : handler.mapControllerBlockEntity.getPosB();
            BlockPos newPos = switch (axis) {
                case "AX" -> new BlockPos(newValue, currentPos.getY(), currentPos.getZ());
                case "AY" -> new BlockPos(currentPos.getX(), newValue, currentPos.getZ());
                case "AZ" -> new BlockPos(currentPos.getX(), currentPos.getY(), newValue);
                case "BX" -> new BlockPos(newValue, currentPos.getY(), currentPos.getZ());
                case "BY" -> new BlockPos(currentPos.getX(), newValue, currentPos.getZ());
                case "BZ" -> new BlockPos(currentPos.getX(), currentPos.getY(), newValue);
                default -> currentPos;
            };

            if (axis.startsWith("A")) {
                handler.mapControllerBlockEntity.setPosA(newPos);
                handler.updatePos(newPos, "posA");
            } else {
                handler.mapControllerBlockEntity.setPosB(newPos);
                handler.updatePos(newPos, "posB");
            }

            textField.setText(String.valueOf(newValue)); // Update the text field with the new value
        } catch (NumberFormatException e) {
            // Reset to the last valid value if the new text is not an integer
            if (axis.charAt(0) == 'A') {
                switch (axis.charAt(1)) {
                    case 'X' -> textField.setText(String.valueOf(handler.mapControllerBlockEntity.getPosA().getX()));
                    case 'Y' -> textField.setText(String.valueOf(handler.mapControllerBlockEntity.getPosA().getY()));
                    case 'Z' -> textField.setText(String.valueOf(handler.mapControllerBlockEntity.getPosA().getZ()));
                }
            } else {
                switch (axis.charAt(1)) {
                    case 'X' -> textField.setText(String.valueOf(handler.mapControllerBlockEntity.getPosB().getX()));
                    case 'Y' -> textField.setText(String.valueOf(handler.mapControllerBlockEntity.getPosB().getY()));
                    case 'Z' -> textField.setText(String.valueOf(handler.mapControllerBlockEntity.getPosB().getZ()));
                }
            }
        }
    }
}
