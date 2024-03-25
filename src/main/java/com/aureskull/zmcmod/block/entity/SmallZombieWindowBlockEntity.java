package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ILinkable;
import com.aureskull.zmcmod.block.custom.SmallZombieWindowBlock;
import com.aureskull.zmcmod.client.InteractionHelper;
import com.aureskull.zmcmod.client.MessageHudOverlay;
import com.aureskull.zmcmod.event.ModKeyInputHandler;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.sound.ModSounds;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmallZombieWindowBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ILinkable {
    public final int MAX_PLANK = 6;
    public int plank = 0;

    private boolean canZombiePassThrough = false;
    private long nextPassThroughTime = 0;


    private BlockPos linkedSpawnerPos;

    private BlockPos linkedZonePos;

    public SmallZombieWindowBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMALL_ZOMBIE_WINDOW_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), 3); //sends an update to clients.
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Small Zombie Doorway");
    }

    @Override
    protected void writeNbt(NbtCompound nbt){
        nbt.putInt("small_zombie_doorway.plank", plank);
        if (linkedSpawnerPos != null) {
            nbt.put("small_zombie_doorway.linked_spawner", NbtHelper.fromBlockPos(linkedSpawnerPos));
        }

        if (linkedZonePos != null) {
            nbt.put("small_zombie_doorway.linked_zone", NbtHelper.fromBlockPos(linkedZonePos));
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);

        if (nbt.contains("small_zombie_doorway.plank"))
            this.plank = nbt.getInt("small_zombie_doorway.plank");

        if (nbt.contains("small_zombie_doorway.linked_spawner")) {
            this.linkedSpawnerPos = NbtHelper.toBlockPos(nbt.getCompound("small_zombie_doorway.linked_spawner"));
        }

        if (nbt.contains("small_zombie_doorway.linked_zone")) {
            linkedZonePos = NbtHelper.toBlockPos(nbt.getCompound("small_zombie_doorway.linked_zone"));
        }
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

        //Show if the player can rebuild the barrier
        if(plank < MAX_PLANK){
            Direction facing = state.get(SmallZombieWindowBlock.FACING);
            Box searchArea = getSearchArea(pos, facing);

            List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, searchArea);
            for (PlayerEntity player : players) {
                if(InteractionHelper.isFacingInteractable(player, facing) &&
                    Math.abs(player.getY() - pos.getY()) <= 1.5) {
                        MessageHudOverlay.setMessage("Hold [" + ModKeyInputHandler.INTERACT.getBoundKeyLocalizedText().getLiteralString() + "] to Rebuild Barrier", 100);
                }
            }
        }

        // Update canZombiePassThrough based on the cooldown
        long currentTime = world.getTime();
        if (currentTime >= nextPassThroughTime) {
            canZombiePassThrough = true;
        } else {
            canZombiePassThrough = false;
        }
    }

    private Box getSearchArea(BlockPos pos, Direction facing) {
        // This creates a search area in front of the block based on its facing direction.
        switch (facing) {
            case NORTH:
                return new Box(pos.north()).expand(0, 1, -.5f); // Adjust these values as needed
            case SOUTH:
                return new Box(pos.south()).expand(0, 1, -.5f);
            case EAST:
                return new Box(pos.east()).expand(-.5f, 1, 0);
            case WEST:
                return new Box(pos.west()).expand(-.5f, 1, 0);
            default:
                return new Box(pos); // Default case, should not happen for horizontal directions
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    public void rebuild(){
        if(plank < MAX_PLANK){
            plank++;
            markDirty();

            assert world != null;
            if (!world.isClient) {
                world.setBlockState(pos, world.getBlockState(pos).with(SmallZombieWindowBlock.PLANKS, Integer.valueOf(plank)), 3);
                world.playSound(null, pos, ModSounds.REBUILD_WINDOW, SoundCategory.BLOCKS, 0.5f, 1.0f);
                world.playSound(null, pos, ModSounds.REBUILD_WINDOW_MONEY, SoundCategory.BLOCKS, 0.5f, 1.0f);
            }
        }

        ZMCMod.LOGGER.info("CURRENT POS : " + this.pos);
        ZMCMod.LOGGER.info("SOUTH : " + new BlockPos(this.pos.getX(), this.pos.getY(),this.pos.getZ() + 1));
    }

    public void removePlank(){
        if(plank > 0){
            plank--;
            markDirty();

            assert world != null;
            if (!world.isClient) {
                world.setBlockState(pos, world.getBlockState(pos).with(SmallZombieWindowBlock.PLANKS, Integer.valueOf(plank)), 3);
                world.playSound(null, pos, ModSounds.SNAP_WINDOW, SoundCategory.BLOCKS, 0.5f, 1.0f);
                world.playSound(null, pos, ModSounds.SNAP_WINDOW, SoundCategory.BLOCKS, 0.5f, 1.0f);
            }
        }
    }

    public int getPlank() {
        return plank;
    }

    public Direction getWindowFacing() {
        BlockState state = this.world.getBlockState(this.pos);
        return state.get(HorizontalFacingBlock.FACING);
    }

    public BlockPos getDirectionPosition(Direction targetDirection){
        Direction direction = getWindowFacing();

        switch (targetDirection) {
            case NORTH:
                switch (direction) {
                    case NORTH:
                        return new BlockPos(this.pos.getX(), this.pos.getY(),this.pos.getZ() - 1);
                    case SOUTH:
                        return new BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ() + 1);
                    case EAST:
                        return new BlockPos(this.pos.getX() + 1, this.pos.getY(), this.pos.getZ());
                    case WEST:
                        return new BlockPos(this.pos.getX() - 1, this.pos.getY(), this.pos.getZ());
                    default:
                        return null;
                }
            case SOUTH:
                switch (direction) {
                    case NORTH:
                        return new BlockPos(this.pos.getX(), this.pos.getY(),this.pos.getZ() + 1);
                    case SOUTH:
                        return new BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ() - 1);
                    case EAST:
                        return new BlockPos(this.pos.getX() - 1, this.pos.getY(), this.pos.getZ());
                    case WEST:
                        return new BlockPos(this.pos.getX() + 1, this.pos.getY(), this.pos.getZ());
                    default:
                        return null;
                }
            case EAST:
                switch (direction) {
                    case NORTH:
                        return new BlockPos(this.pos.getX() + 1, this.pos.getY(),this.pos.getZ());
                    case SOUTH:
                        return new BlockPos(this.pos.getX() - 1, this.pos.getY(), this.pos.getZ());
                    case EAST:
                        return new BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ() + 1);
                    case WEST:
                        return new BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ() - 1);
                    default:
                        return null;
                }
            case WEST:
                switch (direction) {
                    case NORTH:
                        return new BlockPos(this.pos.getX() - 1, this.pos.getY(),this.pos.getZ());
                    case SOUTH:
                        return new BlockPos(this.pos.getX() + 1, this.pos.getY(), this.pos.getZ());
                    case EAST:
                        return new BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ() - 1);
                    case WEST:
                        return new BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ() + 1);
                    default:
                        return null;
                }
            default:
                return null;
        }
    }


    public boolean canPassThrough() {
        return this.plank <= 0 && canZombiePassThrough;
    }

    public void onZombiePassedThrough() {
        if (canZombiePassThrough) {
            long currentTime = world.getTime();
            nextPassThroughTime = currentTime + 40; // 2 seconds cooldown
            canZombiePassThrough = false; // Prevent further passes until cooldown expires
        }
    }


    private void unlinkSpawner(World world) {
        ModMessages.sendRemoveLinkPacket(world, this.getLinkedBlock(ZombieSpawnerBlockEntity.class));

        //Remove from ZombieSpawner the doorway
        BlockEntity existingZombieSpawnerBE = world.getBlockEntity(this.getLinkedBlock(ZombieSpawnerBlockEntity.class));
        if(existingZombieSpawnerBE instanceof ZombieSpawnerBlockEntity)
            ((ZombieSpawnerBlockEntity) existingZombieSpawnerBE).setLinkedBlock(null, SmallZombieWindowBlockEntity.class);

        setLinkedBlock(null, ZombieSpawnerBlockEntity.class);
    }

    private void unlinkZoneController(World world) {
        ModMessages.sendRemoveDoorwayLinkFromZonePacket(world,
                getLinkedBlock(ZoneControllerBlockEntity.class),
                this.getPos());

        //Remove from ZoneController the doorway
        BlockEntity zoneControllerBE = world.getBlockEntity(getLinkedBlock(ZoneControllerBlockEntity.class));
        if(zoneControllerBE instanceof ZoneControllerBlockEntity)
            ((ZoneControllerBlockEntity) zoneControllerBE).removeLinkedBlock(this.getPos(), SmallZombieWindowBlockEntity.class);

        //Remove from server at the end
        setLinkedBlock(null, ZoneControllerBlockEntity.class);
    }

    @Override
    public void unlink(World world, Class<? extends BlockEntity> linkType) {
        if (ZombieSpawnerBlockEntity.class.isAssignableFrom(linkType) && linkedSpawnerPos != null) {
            unlinkSpawner(world);
        } else if (ZoneControllerBlockEntity.class.isAssignableFrom(linkType) && linkedZonePos != null) {
            unlinkZoneController(world);
        }
    }


    @Override
    public void setLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        if (ZombieSpawnerBlockEntity.class.isAssignableFrom(linkType)) {
            this.linkedSpawnerPos = linkedBlockPos;
        } else if (ZoneControllerBlockEntity.class.isAssignableFrom(linkType)) {
            this.linkedZonePos = linkedBlockPos;
        }
        markDirty();
    }

    @Override
    public BlockPos getLinkedBlock(Class<? extends BlockEntity> linkType) {
        if (ZombieSpawnerBlockEntity.class.isAssignableFrom(linkType)) {
            return linkedSpawnerPos;
        } else if (ZoneControllerBlockEntity.class.isAssignableFrom(linkType)) {
            return linkedZonePos;
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