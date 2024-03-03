package com.aureskull.zmcmod.screen;

import com.aureskull.zmcmod.ZMCMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<MapControllerScreenHandler> MAP_CONTROLLER_SCREEN_HANDLER =
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(ZMCMod.MOD_ID, "map_controller"),
            new ExtendedScreenHandlerType<>(MapControllerScreenHandler::new));

    public static void registerScreenHandlers() {
        ZMCMod.LOGGER.info("Registering Screen Handlers for " + ZMCMod.MOD_ID);
    }
}
