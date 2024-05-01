package com.aureskull.zmcmod.block;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.networking.ModMessages;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public interface ILinkable {
    /**
     * Unlinks all block entities of a specified type that are linked to this entity. This method removes the associations
     * between this entity and o-ther linked entities specified by the type.
     *
     * @param sourceBlockEntity The source block entity from which links are being removed.
     * @param linkType The class of the block entities from which links should be removed. This specifies the type of links that will be cleared.
     * @param removeAtDestination If true, also remove the link at the destination block entity, ensuring that the link is bidirectionally cleared.
     */
    default void unlink(BlockEntity sourceBlockEntity, Class<? extends BlockEntity> linkType, boolean removeAtDestination) {
        try{
            List<BlockPos> linkedBlocks = new ArrayList<>(getLink(linkType));
            linkedBlocks.forEach(pos -> removeLink(sourceBlockEntity, pos, linkType, removeAtDestination));
            setLink(new ArrayList<>(), linkType);
            markDirty();
        }catch (Exception e){
            ZMCMod.LOGGER.error("An error occurred when unlink : " + e.getMessage() + e.getStackTrace());
        }
    }

    /**
     * Adds a new block entity to the list of entities linked to this one. This is used for managing one-to-many relationships,
     * allowing multiple entities of the same type to be linked to this block entity.
     *
     * @param linkedBlockPos The position of the block entity to be added to the list of linked entities.
     * @param linkType The class of the block entity that is being linked. This identifies the type of entity to be linked.
     */
    default void addLink(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        try{
            List<BlockPos> linkedBlocks = getLink(linkType);
            if (linkedBlocks != null && !linkedBlocks.contains(linkedBlockPos)) {
                linkedBlocks.add(linkedBlockPos);
                setLink(linkedBlocks, linkType);
                markDirty();
            }
        }catch (Exception e){
            ZMCMod.LOGGER.error("An error occurred when addLink : " + e.getMessage() + e.getStackTrace());
        }
    }

    default void addParentLink(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        try{
            List<BlockPos> linkedBlocks = getParentLink(linkType);
            if (linkedBlocks != null && !linkedBlocks.contains(linkedBlockPos)) {
                linkedBlocks.add(linkedBlockPos);
                setParentLink(linkedBlocks, linkType);
                markDirty();
            }
        }catch (Exception e){
            ZMCMod.LOGGER.error("An error occurred when addLink : " + e.getMessage() + e.getStackTrace());
        }
    }

    default void addChildLink(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        try{
            List<BlockPos> linkedBlocks = getChildLink(linkType);
            if (linkedBlocks != null && !linkedBlocks.contains(linkedBlockPos)) {
                linkedBlocks.add(linkedBlockPos);
                setChildLink(linkedBlocks, linkType);
                markDirty();
            }
        }catch (Exception e){
            ZMCMod.LOGGER.error("An error occurred when addLink : " + e.getMessage() + e.getStackTrace());
        }
    }

    /**
     * Sets the current list of linked block positions for a specified link type. This method updates the internal storage
     * of links to reflect additions or removals.
     *
     * @param blocks The list of block positions that are currently linked.
     * @param linkType The class of the block entities that are linked. This identifies the type of links being set.
     */
    void setLink(List<BlockPos> blocks, Class<? extends BlockEntity> linkType);

    default void setParentLink(List<BlockPos> parents, Class<? extends BlockEntity> linkType){}
    default void setChildLink(List<BlockPos> children, Class<? extends BlockEntity> linkType){}

    /**
     * Removes a link to a block entity from the list of linked entities. This method facilitates the removal of a specific
     * entity from a one-to-many relationship.
     *
     * @param sourceBlockEntity The source block entity from which the link is being removed.
     * @param linkedBlockPos The position of the block entity to be removed from the list of linked entities.
     * @param linkType The class of the block entity that is being unlinked. This identifies the type of link that is being removed.
     * @param removeAtDestination If true, also remove the link at the destination block entity, ensuring that the link is bidirectionally cleared.
     */
    default void removeLink(BlockEntity sourceBlockEntity, BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType, boolean removeAtDestination) {
        List<BlockPos> linkedBlocks = getLink(linkType);
        if (linkedBlocks.remove(linkedBlockPos)) {
            ModMessages.sendSetLinkPacket(sourceBlockEntity.getWorld(), sourceBlockEntity.getPos(), linkedBlocks, linkType);
            setLink(linkedBlocks, linkType);

            if (removeAtDestination) {
                BlockEntity destinationEntity = sourceBlockEntity.getWorld().getBlockEntity(linkedBlockPos);
                if (destinationEntity instanceof ILinkable) {
                    ((ILinkable) destinationEntity).removeLink(destinationEntity, sourceBlockEntity.getPos(), sourceBlockEntity.getClass(), false);
                }
            }

            markDirty();
        }
    }

    default void removeParentLink(BlockEntity sourceBlockEntity, BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType, boolean removeAtDestination) {
        List<BlockPos> linkedBlocks = getParentLink(linkType);
        if (linkedBlocks.remove(linkedBlockPos)) {
            ModMessages.sendSetParentLinkPacket(sourceBlockEntity.getWorld(), sourceBlockEntity.getPos(), linkedBlocks, linkType);
            setParentLink(linkedBlocks, linkType);

            if (removeAtDestination) {
                BlockEntity destinationEntity = sourceBlockEntity.getWorld().getBlockEntity(linkedBlockPos);
                if (destinationEntity instanceof ILinkable) {
                    ((ILinkable) destinationEntity).removeChildLink(destinationEntity, sourceBlockEntity.getPos(), sourceBlockEntity.getClass(), false);
                }
            }

            markDirty();
        }
    }

    default void removeChildLink(BlockEntity sourceBlockEntity, BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType, boolean removeAtDestination) {
        List<BlockPos> linkedBlocks = getChildLink(linkType);
        if (linkedBlocks.remove(linkedBlockPos)) {
            ModMessages.sendSetChildLinkPacket(sourceBlockEntity.getWorld(), sourceBlockEntity.getPos(), linkedBlocks, linkType);
            setChildLink(linkedBlocks, linkType);

            if (removeAtDestination) {
                BlockEntity destinationEntity = sourceBlockEntity.getWorld().getBlockEntity(linkedBlockPos);
                if (destinationEntity instanceof ILinkable) {
                    ((ILinkable) destinationEntity).removeParentLink(destinationEntity, sourceBlockEntity.getPos(), sourceBlockEntity.getClass(), false);
                }
            }

            markDirty();
        }
    }

    /**
     * Retrieves a list of block positions that are currently linked to this entity for a specified link type.
     *
     * @param linkType The class of the block entities whose links are being queried. This helps identify the type of linked entities.
     * @return A list of block positions that are currently linked to this entity of the specified type.
     */
    List<BlockPos> getLink(Class<? extends BlockEntity> linkType);

    default List<BlockPos> getParentLink(Class<? extends BlockEntity> linkType){return null;}
    default List<BlockPos> getChildLink(Class<? extends BlockEntity> linkType){return null;}

    /**
     * Marks the block entity as needing an update, often due to changes in the state that need to be saved or synchronized.
     */
    void markDirty();
}

