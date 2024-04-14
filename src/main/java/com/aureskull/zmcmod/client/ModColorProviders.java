package com.aureskull.zmcmod.client;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ModBlocks;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.entity.BlockEntity;

public class ModColorProviders {

    public static void registerColorProviders() {
        ZMCMod.LOGGER.info("Registering Color Providers for " + ZMCMod.MOD_ID);

        registerZoneControllerColorProvider();
    }

    private static void registerZoneControllerColorProvider(){
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            BlockEntity entity = view.getBlockEntity(pos);
            if (entity instanceof ZoneControllerBlockEntity) {
                ZoneControllerBlockEntity zoneControllerBlockEntity = (ZoneControllerBlockEntity) entity;
                int red = (int)(zoneControllerBlockEntity.getRed() * 255) & 0xFF;
                int green = (int)(zoneControllerBlockEntity.getGreen() * 255) & 0xFF;
                int blue = (int)(zoneControllerBlockEntity.getBlue() * 255) & 0xFF;
                // Now apply the bit shifts correctly on integers
                return (red << 16) + (green << 8) + blue;}
            return -1; // No color change for other layers
        }, ModBlocks.ZONE_CONTROLLER);
    }
}
