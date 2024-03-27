package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ILinkable;
import com.aureskull.zmcmod.entity.ModEntities;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import com.aureskull.zmcmod.networking.ModMessages;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ZombieSpawnerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ILinkable {

    private BlockPos linkedWindowPos;

    public ZombieSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZOMBIE_SPAWNER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Zombie Spawner");
    }

    @Override
    protected void writeNbt(NbtCompound nbt){
        if (linkedWindowPos != null)
            nbt.put("linked_window", NbtHelper.fromBlockPos(linkedWindowPos));

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        if (nbt.contains("linked_window"))
            this.linkedWindowPos =  NbtHelper.toBlockPos(nbt.getCompound("linked_window"));

    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(world.isClient()) {
            return;
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    public BlockPos getLinkedWindow() {
        return this.linkedWindowPos;
    }

    public BlockPos getMapControllerBlockEntityPos() {
        if (getLinkedWindow() != null && world.getBlockEntity(getLinkedWindow()) instanceof SmallZombieWindowBlockEntity window) {
            BlockPos zoneBP = window.getLinkedBlock(ZoneControllerBlockEntity.class);

            if (world.getBlockEntity(zoneBP) instanceof ZoneControllerBlockEntity zoneBE) {
                return findMapControllerRecursively(zoneBE);
            }
        }
        return null; // MapControllerBlockEntity BlockPos not found
    }

    private BlockPos findMapControllerRecursively(ZoneControllerBlockEntity zone) {
        // If this zone is directly linked to a MapControllerBlockEntity, return its BlockPos
        BlockPos mapControllerBP = zone.getLinkedBlock(MapControllerBlockEntity.class);

        if (mapControllerBP != null) {
            return mapControllerBP;
        }else{
            //ZMCMod.LOGGER.info("Map controller not found at zone " + zone.getPos());
        }

        // Otherwise, recursively search through all parent zones
        //ZMCMod.LOGGER.info("Parents in zone " + zone.getPos() + " are : " + zone.getParent(ZoneControllerBlockEntity.class));
        for (BlockPos parentZoneBP : zone.getParent(ZoneControllerBlockEntity.class)) {
            if (world.getBlockEntity(parentZoneBP) instanceof ZoneControllerBlockEntity parentZoneBE) {
                //ZMCMod.LOGGER.info("looking for Map Controller in parent Zone" + parentZoneBE.getPos());
                BlockPos foundBP = findMapControllerRecursively(parentZoneBE);

                if (foundBP != null && world.getBlockEntity(foundBP) instanceof MapControllerBlockEntity) {
                    // If one of the parent zones (or their parents, recursively) is linked to a MapController, return its BlockPos
                    return foundBP;
                }
            }
        }

        // If no MapControllerBlockEntity is found in the hierarchy, return null
        return null;
    }

    public void spawnZombie() {
        BlockPos mapController = this.getMapControllerBlockEntityPos();
        if (!world.isClient && getLinkedWindow() != null &&  mapController != null && world.getBlockEntity(mapController) instanceof  MapControllerBlockEntity) {
            // Logic to spawn the zombie
            StandingZombieEntity zombie = ModEntities.STANDING_ZOMBIE.create(world);
            zombie.setTargetBlockPos(getLinkedWindow());
            zombie.setMapControllerBlockPos(mapController);

            if (zombie != null) {
                zombie.setPosition(getPos().getX() + 0.5, getPos().getY() + 1, getPos().getZ() + 0.5);
                world.spawnEntity(zombie);
            }
        }
    }

    public void unlinkDoorway(World world) {
        ModMessages.sendRemoveLinkPacket(world, this.getLinkedBlock(SmallZombieWindowBlockEntity.class));

        BlockEntity existingDoorwayEntity = world.getBlockEntity(this.getLinkedBlock(SmallZombieWindowBlockEntity.class));
        if (existingDoorwayEntity instanceof SmallZombieWindowBlockEntity)
            ((SmallZombieWindowBlockEntity) existingDoorwayEntity).setLinkedBlock(null, ZombieSpawnerBlockEntity.class);

        setLinkedBlock(null, SmallZombieWindowBlockEntity.class);
    }

    @Override
    public void unlink(World world, Class<? extends BlockEntity> linkType) {
        if(SmallZombieWindowBlockEntity.class.isAssignableFrom(linkType) && getLinkedWindow() != null){
            unlinkDoorway(world);
        }
    }

    @Override
    public void setLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        if(SmallZombieWindowBlockEntity.class.isAssignableFrom(linkType)) {
            this.linkedWindowPos = linkedBlockPos;
        }

        markDirty();
    }

    @Override
    public @Nullable BlockPos getLinkedBlock(Class<? extends BlockEntity> linkType) {
        if(SmallZombieWindowBlockEntity.class.isAssignableFrom(linkType)) {
            return getLinkedWindow();
        }

        return null;
    }

    @Override
    public void addLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {

    }

    @Override
    public void removeLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {

    }

    @Override
    public List<BlockPos> getAllLinkedBlocks(Class<? extends BlockEntity> linkType) {
        return null;
    }
}