package com.aureskull.zmcmod.block.entity.door;

import com.aureskull.zmcmod.block.IBuyable;
import com.aureskull.zmcmod.block.ILinkable;
import com.aureskull.zmcmod.block.custom.door.DoorPartBlock;
import com.aureskull.zmcmod.block.entity.ModBlockEntities;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.client.InteractionHelper;
import com.aureskull.zmcmod.client.overlay.MessageHudOverlay;
import com.aureskull.zmcmod.event.ModKeyInputHandler;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.door.DoorScreenHandler;
import com.aureskull.zmcmod.sound.ModSounds;
import com.aureskull.zmcmod.util.PlayerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DoorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ILinkable, IBuyable {
    private BlockPos masterPos;
    private int price;
    private List<BlockPos> doorParts = new ArrayList<>();
    private List<BlockPos> linkedZoneControllers = new ArrayList<>();


    public DoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DOOR_BLOCK_ENTITY, pos, state);
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
        return Text.literal("Door");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return player.isCreative() ? new DoorScreenHandler(syncId, this) : null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt){
        if (masterPos != null)
            nbt.put("door_part.masterPos", NbtHelper.fromBlockPos(masterPos));

        if(price > 0)
            nbt.putInt("door_part.price", price);


        NbtList doorPartsNbt = new NbtList();
        for (BlockPos pos : doorParts)
            doorPartsNbt.add(NbtHelper.fromBlockPos(pos));
        if(doorPartsNbt.size() > 0)
            nbt.put("door_part.door_parts", doorPartsNbt);


        NbtList zoneControllersNbt = new NbtList();
        for (BlockPos pos : linkedZoneControllers)
            zoneControllersNbt.add(NbtHelper.fromBlockPos(pos));
        if(zoneControllersNbt.size() > 0)
            nbt.put("door_part.linked_zone_controllers", zoneControllersNbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        if (nbt.contains("door_part.masterPos"))
            masterPos = NbtHelper.toBlockPos(nbt.getCompound("door_part.masterPos"));

        if (nbt.contains("door_part.price"))
            price = nbt.getInt("door_part.price");

        NbtList windowsList = nbt.getList("door_part.door_parts", 10);
        doorParts = new ArrayList<>();
        for (int i = 0; i < windowsList.size(); i++) {
            doorParts.add(NbtHelper.toBlockPos(windowsList.getCompound(i)));
        }

        NbtList zoneControllersList = nbt.getList("door_part.linked_zone_controllers", 10);
        linkedZoneControllers = new ArrayList<>();
        for (int i = 0; i < zoneControllersList.size(); i++) {
            linkedZoneControllers.add(NbtHelper.toBlockPos(zoneControllersList.getCompound(i)));
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
        if(!world.isClient){

        }else{
            if(masterPos == null){
                MinecraftClient client = MinecraftClient.getInstance();
                if(client.player == null) return;

                Direction facing = state.get(DoorPartBlock.FACING);
                Box searchArea = getSearchArea(pos, facing);
                PlayerEntity player = client.player;
                HitResult hit = InteractionHelper.rayTrace(player, searchArea);

                if(!player.isSpectator()){
                    if (hit.getType() == HitResult.Type.BLOCK && searchArea.contains(player.getPos())) {
                        if(PlayerHelper.isPlaying(player)){
                            MessageHudOverlay.setMessage("Hold [" + ModKeyInputHandler.INTERACT.getBoundKeyLocalizedText().getLiteralString() + "] to Open Door [Cost: " + price + "]", Formatting.WHITE, 100);
                        }else{
                            MessageHudOverlay.setMessage("You are not playing in this game!", Formatting.DARK_RED, 100);
                        }
                    }
                }
            }
        }
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
        this.markDirty();
    }

    public BlockPos getMasterPos() {
        return masterPos;
    }

    public DoorBlockEntity getMasterBlockEntity() {
        if(masterPos == null)
            return this;

        BlockEntity be = world.getBlockEntity(masterPos);
        if(be != null && be instanceof DoorBlockEntity masterDoorBlockEntity){
            return masterDoorBlockEntity;
        }

        return null;
    }

    public void buildDoor(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            //Add itself to the parts blocs
            doorParts.add(this.getPos());

            //If not enough space => Destroy the door
            if(!isBuildable(state)){
                destroyDoor();
                return;
            }

            Direction facing = state.get(HorizontalFacingBlock.FACING);
            // Determining the increment for positioning blocks relative to the door's orientation
            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 2; y++) {
                    BlockPos blockPos;
                    BlockPos externalBlockPos;
                    DoorPartBlock.DoorSide side;

                    if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                        blockPos = pos.add(x, y, 0);  // Expands east-west
                        // Determine side based on X position relative to facing
                        if (x == 0) {
                            side = DoorPartBlock.DoorSide.CENTER;
                        } else if ((x == 1 && facing == Direction.NORTH) || (x == -1 && facing == Direction.SOUTH)) {
                            side = DoorPartBlock.DoorSide.LEFT;
                        } else {
                            side = DoorPartBlock.DoorSide.RIGHT;
                        }
                    } else {
                        blockPos = pos.add(0, y, x);  // Expands north-south
                        // Determine side based on Z position relative to facing
                        if (x == 0) {
                            side = DoorPartBlock.DoorSide.CENTER;
                        } else if ((x == 1 && facing == Direction.WEST) || (x == -1 && facing == Direction.EAST)) {
                            side = DoorPartBlock.DoorSide.RIGHT;
                        } else {
                            side = DoorPartBlock.DoorSide.LEFT;
                        }
                    }

                    createDoorPart(blockPos, state, side);

                    //Create external collider
                    if(x == -1 || x == 1) {
                        //Determine coordinates
                        if (facing == Direction.NORTH || facing == Direction.SOUTH){
                            if(facing == Direction.NORTH) externalBlockPos = pos.add(x, y, 1);
                            else externalBlockPos = pos.add(x, y, -1);
                        }else{
                            if(facing == Direction.WEST )externalBlockPos = pos.add(1, y, x);
                            else externalBlockPos = pos.add(-1, y, x);
                        }

                        DoorPartBlock.DoorSide colliderSide;
                        //Place them
                        switch (facing){
                            case NORTH:
                                colliderSide = x == 1 ? DoorPartBlock.DoorSide.EXTERNAL_COLLIDER_LEFT : DoorPartBlock.DoorSide.EXTERNAL_COLLIDER_RIGHT;
                                break;

                            case SOUTH:
                                colliderSide = x == -1 ? DoorPartBlock.DoorSide.EXTERNAL_COLLIDER_LEFT : DoorPartBlock.DoorSide.EXTERNAL_COLLIDER_RIGHT;
                                break;

                            case EAST:
                                colliderSide = x == 1 ? DoorPartBlock.DoorSide.EXTERNAL_COLLIDER_LEFT : DoorPartBlock.DoorSide.EXTERNAL_COLLIDER_RIGHT;
                                break;

                            case WEST:
                                colliderSide = x == -1 ? DoorPartBlock.DoorSide.EXTERNAL_COLLIDER_LEFT : DoorPartBlock.DoorSide.EXTERNAL_COLLIDER_RIGHT;
                                break;

                            default:
                                colliderSide = DoorPartBlock.DoorSide.EXTERNAL_COLLIDER_LEFT;
                        }

                        createDoorPart(externalBlockPos, state, colliderSide);
                    }
                }
            }
            this.markDirty();
        }
    }

    private void createDoorPart(BlockPos blockPos, BlockState state, DoorPartBlock.DoorSide side){
        if (!blockPos.equals(pos) && world.getBlockState(blockPos).isAir()) {
            world.setBlockState(blockPos, state.with(DoorPartBlock.SIDE, side), 3);

            BlockEntity newBlockEntity = world.getBlockEntity(blockPos);
            if (newBlockEntity instanceof DoorBlockEntity doorBlockEntity) {
                doorBlockEntity.setMasterPos(this.getPos());
            }
            doorParts.add(blockPos);
        }
    }

    private boolean isBuildable(BlockState state){
        if (!world.isClient){
            Direction facing = state.get(HorizontalFacingBlock.FACING);
            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 2; y++) {
                    BlockPos blockPos;

                    if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                        blockPos = pos.add(x, y, 0);
                        if(blockPos == pos) continue;
                        if(!world.getBlockState(blockPos).isAir()) return false;
                    } else {
                        blockPos = pos.add(0, y, x);
                        if(blockPos == pos) continue;
                        if(!world.getBlockState(blockPos).isAir()) return false;
                    }
                }
            }
        }
        return true;
    }

    public void destroyDoor(){
        if (world == null || world.isClient) return;

        if(masterPos != null){
            BlockEntity be = world.getBlockEntity(masterPos);
            if(be != null && be instanceof DoorBlockEntity masterDoorBlockEntity){
                masterDoorBlockEntity.destroyDoor();
            }
            return;
        }

        for(BlockPos pos : doorParts){
            BlockEntity newBlockEntity = world.getBlockEntity(pos);
            if (newBlockEntity instanceof DoorBlockEntity) {
                world.removeBlock(pos, false);
            }
        }

        unlink(world, ZoneControllerBlockEntity.class);
    }

    public void openDoor() {
        if(world.isClient) return;

        if(masterPos != null){
            BlockEntity be = world.getBlockEntity(masterPos);
            if(be != null && be instanceof DoorBlockEntity masterDoorBlockEntity){
                masterDoorBlockEntity.openDoor();
            }
            return;
        }

        BlockState blockState = null;
        for(BlockPos pos : doorParts){
            if(world.getBlockEntity(pos) instanceof DoorBlockEntity){
                blockState = world.getBlockState(pos);
                world.setBlockState(pos, blockState.with(DoorPartBlock.OPEN, true), 3);
            }
        }

        if(blockState != null && !blockState.get(DoorPartBlock.OPEN)) world.playSound(null, pos, ModSounds.DOOR_OPEN, SoundCategory.BLOCKS, 0.5f, 1.0f);
    }

    public void closeDoor() {
        if(world.isClient) return;

        if(masterPos != null){
            BlockEntity be = world.getBlockEntity(masterPos);
            if(be != null && be instanceof DoorBlockEntity masterDoorBlockEntity){
                masterDoorBlockEntity.closeDoor();
            }
            return;
        }

        for(BlockPos pos : doorParts){
            if(world.getBlockEntity(pos) instanceof DoorBlockEntity){
                BlockState blockState = world.getBlockState(pos);
                world.setBlockState(pos, blockState.with(DoorPartBlock.OPEN, false), 3);
            }
        }
    }

    private Box getSearchArea(BlockPos pos, Direction facing) {
        switch (facing) {
            case NORTH : return new Box(pos.north().add(0, 0, 1)).expand(.7f, 1, .5f);
            case SOUTH : return new Box(pos.south().add(0, 0, -1)).expand(.7f, 1, .5f);
            case EAST : return new Box(pos.east().add(-1, 0, 0)).expand(.5f, 1, .7f);
            case WEST : return new Box(pos.west().add(1, 0, 0)).expand(.5f, 1, .7f);
            default : return new Box(pos);
        }
    }

    public int getPrice() {
        return getMasterBlockEntity().price;
    }

    public void setPrice(int price) {
        if(masterPos == null) {
            this.price = price;
            markDirty();
        }else getMasterBlockEntity().setPrice(price);
    }

    private void unlinkZone(World world, BlockPos zonePos){
        ModMessages.sendRemoveDoorLinkFromZonePacket(world, zonePos, getPos());

        //Remove from SmallZombieDoorway the zone
        BlockEntity zoneBE = world.getBlockEntity(zonePos);
        if (zoneBE instanceof ZoneControllerBlockEntity zoneControllerBlockEntity)
            zoneControllerBlockEntity.removeLinkedBlock(getPos(), DoorBlockEntity.class);

        removeLinkedBlock(zonePos, ZoneControllerBlockEntity.class);
    }

    private void addLinkedZoneController(BlockPos zoneControllerBlockPos) {
        if (zoneControllerBlockPos != this.getPos() && !linkedZoneControllers.contains(zoneControllerBlockPos) && world.getBlockEntity(zoneControllerBlockPos) instanceof ZoneControllerBlockEntity)
            linkedZoneControllers.add(zoneControllerBlockPos);
    }

    private void removeLinkedZoneController(BlockPos zoneControllerBlockPos) {
        if(linkedZoneControllers.contains(zoneControllerBlockPos))
            linkedZoneControllers.remove(zoneControllerBlockPos);
    }

    @Override
    public void unlink(World world, Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType) && linkedZoneControllers != null && masterPos == null){
            List<BlockPos> zones = new CopyOnWriteArrayList<>(getAllLinkedBlocks(ZoneControllerBlockEntity.class));
            for (BlockPos zone : zones)
                unlinkZone(world, zone);
        }
    }

    @Override
    public void setLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {

    }

    @Override
    public @Nullable BlockPos getLinkedBlock(Class<? extends BlockEntity> linkType) {
        return null;
    }

    @Override
    public void addLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        if(masterPos != null) getMasterBlockEntity().addLinkedBlock(linkedBlockPos, linkType);

        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType)){
            addLinkedZoneController(linkedBlockPos);
        }

        markDirty();
    }

    @Override
    public void removeLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        if(masterPos != null) getMasterBlockEntity().removeLinkedBlock(linkedBlockPos, linkType);

        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType)){
            removeLinkedZoneController(linkedBlockPos);
        }

        markDirty();
    }

    @Override
    public List<BlockPos> getAllLinkedBlocks(Class<? extends BlockEntity> linkType) {
        if(masterPos != null) getMasterBlockEntity().getAllLinkedBlocks(linkType);

        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType)){
            return linkedZoneControllers;
        }

        return null;
    }

    @Override
    public void buyEvent(PlayerEntity player) {
        openDoor();
    }
}
