package com.aureskull.zmcmod.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ILinkable {
    /**
     * Unlinks this entity from its current connection.
     * @param world The world where the entity exists.
     */
    void unlink(World world, Class<? extends BlockEntity> linkType);

    /**
     * Sets a new link between this entity and another entity.
     * @param linkedBlockPos The position of the block entity to link with.
     */
    void setLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType);

    /**
     * Retrieves the position of the currently linked block entity, if any.
     * @return The position of the linked block entity, or null if there is no link.
     */
    @Nullable
    BlockPos getLinkedBlock(Class<? extends BlockEntity> linkType);
}
