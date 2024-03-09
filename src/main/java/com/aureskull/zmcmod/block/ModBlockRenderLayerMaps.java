package com.aureskull.zmcmod.block;

import com.aureskull.zmcmod.ZMCMod;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class ModBlockRenderLayerMaps {

    public static void putBlocks(){
        ZMCMod.LOGGER.info("Putting blocks for " + ZMCMod.MOD_ID);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ZONE_CONTROLLER, RenderLayer.getTranslucent());
    }
}
