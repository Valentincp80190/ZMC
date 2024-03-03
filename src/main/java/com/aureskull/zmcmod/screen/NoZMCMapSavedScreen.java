package com.aureskull.zmcmod.screen;

import com.aureskull.zmcmod.ZMCMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class NoZMCMapSavedScreen extends Screen {
    private static final Identifier LOGO_TEXTURE = new Identifier(ZMCMod.MOD_ID, "logo");

    private final Screen parent;

    public ButtonWidget create_map = ButtonWidget.builder(Text.literal("Create a map for this world"), button -> {
                System.out.println("Create a map");
                //Do some stuff
            })
            .dimensions(width / 2 - 205, 20, 200, 20)
            //.tooltip(Tooltip.of(Text.literal("Allow a player to create a ZMC map on the current world")))
            .build();

    public NoZMCMapSavedScreen(Screen parent) {
        super(Text.literal("No ZMC Map found"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        create_map.setPosition(width / 2 - 100, height/2);

        addDrawableChild(create_map);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawGuiTexture(LOGO_TEXTURE, width / 2 - 115, 50, 230, 49);

        int yOffset = 40;
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("There is no ZMC map into this world."), width / 2, height / 2 - yOffset, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Would you like to create one?"), width / 2, height / 2 + textRenderer.fontHeight + 4 - yOffset, 0xffffff);

    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}