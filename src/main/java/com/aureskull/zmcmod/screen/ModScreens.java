package com.aureskull.zmcmod.screen;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.screen.mapcontroller.MapControllerScreen;
import com.aureskull.zmcmod.screen.zonecontroller.ZoneControllerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ModScreens {
    public static void registerScreens() {
        ZMCMod.LOGGER.info("Registering Screens for " + ZMCMod.MOD_ID);

        HandledScreens.register(ModScreenHandlers.MAP_CONTROLLER_SCREEN_HANDLER, MapControllerScreen::new);
        HandledScreens.register(ModScreenHandlers.ZONE_CONTROLLER_SCREEN_HANDLER, ZoneControllerScreen::new);
    }
}
