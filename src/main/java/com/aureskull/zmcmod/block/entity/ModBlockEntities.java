package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<MapControllerBlockEntity> MAP_CONTROLLER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ZMCMod.MOD_ID, "map_controller_be"),//be = block entity
                    FabricBlockEntityTypeBuilder.create(MapControllerBlockEntity::new,
                            ModBlocks.MAP_CONTROLLER).build());

    public static void registerBlockEntities(){
        ZMCMod.LOGGER.info("Registering Block Entities for " + ZMCMod.MOD_ID);
    }
}
