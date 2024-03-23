package com.aureskull.zmcmod.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ILinkable {
    /**
     * Unlinks this entity from its current connection of a specified type. This method removes the association
     * between this entity and another linked entity specified by the type.
     *
     * @param world The world where the entity exists. This is required to access the linked entity and perform the unlinking.
     * @param linkType The class of the linked entity from which this entity should be unlinked. This helps in identifying the type of link to be removed.
     */
    void unlink(World world, Class<? extends BlockEntity> linkType);

    /**
     * Sets (or replaces) a new link between this entity and another block entity. If a link of the specified type already exists,
     * it will be replaced with this new link. This method is typically used for one-to-one relationships.
     *
     * @param linkedBlockPos The position of the block entity to link with. This specifies where the linked entity is located in the world.
     * @param linkType The class of the block entity that is being linked. This parameter specifies the type of link that is being established.
     */
    void setLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType);

    /**
     * Retrieves the position of the currently linked block entity of a specific type, if any. This method is used to get the position of an entity
     * that has a one-to-one relationship with this entity.
     *
     * @param linkType The class of the block entity whose link is being queried. This specifies the type of the linked entity.
     * @return The position of the linked block entity, or null if there is no entity of the specified type linked to this one.
     */
    @Nullable
    BlockPos getLinkedBlock(Class<? extends BlockEntity> linkType);

    /**
     * Adds a new block entity to the list of linked entities of a specified type. This method is suitable for managing one-to-many relationships,
     * allowing multiple entities of the same type to be linked to this one.
     *
     * @param linkedBlockPos The position of the block entity to be added to the list of linked entities. This specifies where the new linked entity is located in the world.
     * @param linkType The class of the block entity that is being linked. This parameter specifies the type of link that is being established.
     */
    void addLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType);

    /**
     * Removes a block entity from the list of linked entities of a specified type. This method allows for the removal of a specific entity from a one-to-many relationship.
     *
     * @param linkedBlockPos The position of the block entity to be removed from the list of linked entities. This specifies the location of the entity to be unlinked.
     * @param linkType The class of the block entity that is being unlinked. This parameter identifies the type of link that is being removed.
     */
    void removeLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType);

    /**
     * Retrieves all linked block positions of the specified type. This method is used to obtain a list of positions
     * representing all block entities of a certain type that are linked to this entity. It is particularly useful for
     * managing and accessing one-to-many relationships.
     *
     * @param linkType The class of the block entities whose linked positions are being requested. This parameter specifies
     * the type of the linked entities whose positions are to be retrieved.
     * @return A list of {@link BlockPos} representing the positions of all linked block entities of the specified type.
     * If there are no linked entities of this type, an empty list is returned.
     */
    List<BlockPos> getAllLinkedBlocks(Class<? extends BlockEntity> linkType);
}

