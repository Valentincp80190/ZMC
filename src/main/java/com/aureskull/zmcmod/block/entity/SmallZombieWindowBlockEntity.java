package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ILinkable;
import com.aureskull.zmcmod.block.custom.SmallZombieWindowBlock;
import com.aureskull.zmcmod.client.InteractionHelper;
import com.aureskull.zmcmod.client.overlay.MessageHudOverlay;
import com.aureskull.zmcmod.event.ModKeyInputHandler;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.sound.ModSounds;
import com.aureskull.zmcmod.util.PlayerData;
import com.aureskull.zmcmod.util.PlayerHelper;
import com.aureskull.zmcmod.util.StateSaverAndLoader;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SmallZombieWindowBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ILinkable {
    private final int MAX_PLANK = 6;
    private int plank = 0;

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
            try{
                //Show if the player can rebuild the barrier
                if(plank < MAX_PLANK){
                    Direction facing = state.get(SmallZombieWindowBlock.FACING);
                    Box searchArea = getSearchArea(pos, facing);

                    List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, searchArea);
                    for (PlayerEntity player : players) {
                        if(InteractionHelper.isFacingInteractable(player, facing) &&
                                Math.abs(player.getY() - pos.getY()) <= 1.5) {
                            if(PlayerHelper.isPlaying(player)){
                                MessageHudOverlay.setMessage("Hold [" + ModKeyInputHandler.INTERACT.getBoundKeyLocalizedText().getLiteralString() + "] to Rebuild Barrier", Formatting.WHITE, 100);
                            }else{
                                MessageHudOverlay.setMessage("You are not playing in this game!", Formatting.DARK_RED, 100);
                            }
                        }
                    }
                }
            }catch (Exception e){
                ZMCMod.LOGGER.error("An error occurred in the SmallZombieWindowBlockEntity tick method :" + e.getMessage() + e.getStackTrace());
            }
        }else{
            // Update canZombiePassThrough based on the cooldown
            long currentTime = world.getTime();
            if (currentTime >= nextPassThroughTime) {
                canZombiePassThrough = true;
            } else {
                canZombiePassThrough = false;
            }
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

    public void rebuild(ServerPlayerEntity player){
        if(plank < MAX_PLANK){
            plank++;
            markDirty();

            assert world != null;
            if (!world.isClient) {
                world.setBlockState(pos, world.getBlockState(pos).with(SmallZombieWindowBlock.PLANKS, plank), 3);
                world.playSound(null, pos, ModSounds.REBUILD_WINDOW, SoundCategory.BLOCKS, 0.5f, 1.0f);

                PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                playerData.addMoney(10);
                world.playSound(null, pos, ModSounds.REBUILD_WINDOW_MONEY, SoundCategory.BLOCKS, 0.5f, 1.0f);
            }
        }
    }

    public void removePlank(){
        if(plank > 0){
            plank--;
            markDirty();

            assert world != null;
            if (!world.isClient) {
                world.setBlockState(pos, world.getBlockState(pos).with(SmallZombieWindowBlock.PLANKS, plank), 3);
                world.playSound(null, pos, ModSounds.SNAP_WINDOW, SoundCategory.BLOCKS, 0.5f, 1.0f);
                world.playSound(null, pos, ModSounds.SNAP_WINDOW, SoundCategory.BLOCKS, 0.5f, 1.0f);
            }
        }
    }

    public int getPlank() {
        return plank;
    }

    public void resetPlank() {
        this.plank = MAX_PLANK;
        if(world != null) world.setBlockState(pos, world.getBlockState(pos).with(SmallZombieWindowBlock.PLANKS, plank), 3);
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
            nextPassThroughTime = currentTime + 20; // 1 second cooldown
            canZombiePassThrough = false; // Prevent further passes until cooldown expires
        }
    }

    @Override
    public void setLink(List<BlockPos> blocks, Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType))
            linkedZonePos = blocks.size() > 0 ? blocks.get(0) : null;

        else if(ZombieSpawnerBlockEntity.class.isAssignableFrom(linkType))
            linkedSpawnerPos = blocks.size() > 0 ? blocks.get(0) : null;
    }

    @Override
    public List<BlockPos> getLink(Class<? extends BlockEntity> linkType) {
        List<BlockPos> blockPosList = new ArrayList<>();

        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType)){
            if(linkedZonePos != null) blockPosList.add(linkedZonePos);
            return blockPosList;
        }

        if(ZombieSpawnerBlockEntity.class.isAssignableFrom(linkType)){
            if(linkedSpawnerPos != null) blockPosList.add(linkedSpawnerPos);
            return blockPosList;
        }

        return null;
    }
}