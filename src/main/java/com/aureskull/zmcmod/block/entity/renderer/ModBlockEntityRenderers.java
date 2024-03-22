package com.aureskull.zmcmod.block.entity.renderer;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.ModBlockEntities;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ModBlockEntityRenderers {

    public static void registerBlockEntityRenderers() {
        ZMCMod.LOGGER.info("Registering Block Entity Renderer for " + ZMCMod.MOD_ID);

        BlockEntityRendererFactories.register(ModBlockEntities.ZONE_CONTROLLER_BLOCK_ENTITY, ZoneControllerEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SMALL_ZOMBIE_DOORWAY_BLOCK_ENTITY, SmallZombieDoorwayEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.MAP_CONTROLLER_BLOCK_ENTITY, MapControllerEntityRenderer::new);
    }
}
