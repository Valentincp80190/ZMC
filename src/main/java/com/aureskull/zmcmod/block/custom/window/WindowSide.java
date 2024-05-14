package com.aureskull.zmcmod.block.custom.window;

import net.minecraft.util.StringIdentifiable;

public enum WindowSide implements StringIdentifiable {
    LEFT, RIGHT;

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}