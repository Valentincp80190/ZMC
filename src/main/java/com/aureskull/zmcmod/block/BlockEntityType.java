package com.aureskull.zmcmod.block;

import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.block.entity.ZombieSpawnerBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
import net.minecraft.block.entity.BlockEntity;

public enum BlockEntityType {
    MAP_CONTROLLER(MapControllerBlockEntity.class),
    ZONE_CONTROLLER(ZoneControllerBlockEntity.class),
    DOOR(DoorBlockEntity.class),
    SMALL_ZOMBIE_WINDOW(SmallZombieWindowBlockEntity.class),
    ZOMBIE_SPAWNER(ZombieSpawnerBlockEntity.class);

    private final Class<? extends BlockEntity> blockEntityClass;

    BlockEntityType(Class<? extends BlockEntity> blockEntityClass) {
        this.blockEntityClass = blockEntityClass;
    }

    public Class<? extends BlockEntity> getBlockEntityClass() {
        return this.blockEntityClass;
    }

    public static BlockEntityType fromClass(Class<? extends BlockEntity> blockEntityClass) {
        for (BlockEntityType type : values()) {
            if (type.blockEntityClass.equals(blockEntityClass)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unrecognized class: " + blockEntityClass);
    }
}
