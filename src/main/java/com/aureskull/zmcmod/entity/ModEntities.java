package com.aureskull.zmcmod.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<StandingZombieEntity> STANDING_ZOMBIE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ZMCMod.MOD_ID, "standing_zombie"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, StandingZombieEntity::new)
                    .dimensions(EntityDimensions.fixed(.7f, 2f)).build());

    public static void registerModEntities() {
        ZMCMod.LOGGER.info("Registering Entities for " + ZMCMod.MOD_ID);
    }

}
