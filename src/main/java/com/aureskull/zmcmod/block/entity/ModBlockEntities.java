package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ModBlocks;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
import com.aureskull.zmcmod.block.entity.window.MediumZombieWindowBlockEntity;
import com.aureskull.zmcmod.block.entity.window.SmallZombieWindowBlockEntity;
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

    public static final BlockEntityType<ZoneControllerBlockEntity> ZONE_CONTROLLER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ZMCMod.MOD_ID, "zone_controller_be"),
                    FabricBlockEntityTypeBuilder.create(ZoneControllerBlockEntity::new,
                            ModBlocks.ZONE_CONTROLLER).build());

    public static final BlockEntityType<SmallZombieWindowBlockEntity> SMALL_ZOMBIE_WINDOW_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ZMCMod.MOD_ID, "small_zombie_doorway_be"),
                    FabricBlockEntityTypeBuilder.create(SmallZombieWindowBlockEntity::new,
                            ModBlocks.SMALL_ZOMBIE_WINDOW).build());

    public static final BlockEntityType<MediumZombieWindowBlockEntity> MEDIUM_ZOMBIE_WINDOW_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ZMCMod.MOD_ID, "medium_zombie_doorway_be"),
                    FabricBlockEntityTypeBuilder.create(MediumZombieWindowBlockEntity::new,
                            ModBlocks.MEDIUM_ZOMBIE_WINDOW).build());

    public static final BlockEntityType<ZombieSpawnerBlockEntity> ZOMBIE_SPAWNER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ZMCMod.MOD_ID, "zombie_spawner_be"),
                    FabricBlockEntityTypeBuilder.create(ZombieSpawnerBlockEntity::new,
                            ModBlocks.ZOMBIE_SPAWNER).build());

    public static final BlockEntityType<DoorBlockEntity> MEDIUM_DOOR_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ZMCMod.MOD_ID, "medium_door_be"),
                    FabricBlockEntityTypeBuilder.create(DoorBlockEntity::new,
                            ModBlocks.MEDIUM_ZOMBIE_WINDOW).build());

    public static final BlockEntityType<DoorBlockEntity> DOOR_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ZMCMod.MOD_ID, "door_be"),
                    FabricBlockEntityTypeBuilder.create(DoorBlockEntity::new,
                            ModBlocks.DOOR_PART).build());

    public static void registerBlockEntities(){
        ZMCMod.LOGGER.info("Registering Block Entities for " + ZMCMod.MOD_ID);
    }
}
