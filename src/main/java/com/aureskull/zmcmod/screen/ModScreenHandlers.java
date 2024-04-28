package com.aureskull.zmcmod.screen;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.screen.door.DoorScreenHandler;
import com.aureskull.zmcmod.screen.mapcontroller.MapControllerScreenHandler;
import com.aureskull.zmcmod.screen.zonecontroller.ZoneControllerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<MapControllerScreenHandler> MAP_CONTROLLER_SCREEN_HANDLER =
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(ZMCMod.MOD_ID, "map_controller"),
            new ExtendedScreenHandlerType<>(MapControllerScreenHandler::new));

    public static final ScreenHandlerType<ZoneControllerScreenHandler> ZONE_CONTROLLER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(ZMCMod.MOD_ID, "zone_controller"),
                    new ExtendedScreenHandlerType<>(ZoneControllerScreenHandler::new));

    public static final ScreenHandlerType<DoorScreenHandler> DOOR_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(ZMCMod.MOD_ID, "door"),
                    new ExtendedScreenHandlerType<>(DoorScreenHandler::new));

    public static void registerScreenHandlers() {
        ZMCMod.LOGGER.info("Registering Screen Handlers for " + ZMCMod.MOD_ID);
    }
}
