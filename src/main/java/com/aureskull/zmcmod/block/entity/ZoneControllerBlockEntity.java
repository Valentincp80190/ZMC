package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ILinkable;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.zonecontroller.ZoneControllerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ZoneControllerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ILinkable {
    public BlockPos posA = new BlockPos(pos.getX() - 5, pos.getY() + 1, pos.getZ() - 5);
    public BlockPos posB = new BlockPos(pos.getX() + 5, pos.getY() + 5, pos.getZ() + 5);

    public float red = 1f;
    public float green = 1f;
    public float blue = 1f;

    private BlockPos linkedMapController = null;


    private List<BlockPos> linkedParentZoneControllers = new ArrayList<>();
    private List<BlockPos> linkedChildZoneControllers = new ArrayList<>();

    private List<BlockPos> linkedWindows = new ArrayList<>();

    public ZoneControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONE_CONTROLLER_BLOCK_ENTITY, pos, state);
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
        return Text.literal("Zone Controller");
    }

    @Override
    protected void writeNbt(NbtCompound nbt){
        nbt.putFloat("zone_controller.red", red);
        nbt.putFloat("zone_controller.green", green);
        nbt.putFloat("zone_controller.blue", blue);

        nbt.putFloat("zone_controller.posa.x", posA.getX());
        nbt.putFloat("zone_controller.posa.y", posA.getY());
        nbt.putFloat("zone_controller.posa.z", posA.getZ());

        nbt.putFloat("zone_controller.posb.x", posB.getX());
        nbt.putFloat("zone_controller.posb.y", posB.getY());
        nbt.putFloat("zone_controller.posb.z", posB.getZ());

        if (linkedMapController != null) {
            nbt.put("zone_controller.linked_map_controller", NbtHelper.fromBlockPos(linkedMapController));
        }

        NbtList windowsList = new NbtList();
        for (BlockPos pos : linkedWindows)
            windowsList.add(NbtHelper.fromBlockPos(pos));
        if(windowsList.size() > 0)
            nbt.put("zone_controller.linked_windows", windowsList);


        NbtList zoneList = new NbtList();
        for (BlockPos pos : linkedChildZoneControllers)
            zoneList.add(NbtHelper.fromBlockPos(pos));
        if(zoneList.size() > 0)
            nbt.put("zone_controller.linked_child_zones", zoneList);


        NbtList zoneParentList = new NbtList();
        for (BlockPos pos : linkedParentZoneControllers)
            zoneParentList.add(NbtHelper.fromBlockPos(pos));
        if(zoneParentList.size() > 0)
            nbt.put("zone_controller.linked_parent_zones", zoneParentList);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);

        if (nbt.contains("zone_controller.red"))
            this.red = nbt.getFloat("zone_controller.red");

        if (nbt.contains("zone_controller.green"))
            this.green = nbt.getFloat("zone_controller.green");

        if (nbt.contains("zone_controller.blue"))
            this.blue = nbt.getFloat("zone_controller.blue");

        if (nbt.contains("zone_controller.posa.x", 99) ||
                nbt.contains("zone_controller.posa.y", 99) ||
                nbt.contains("zone_controller.posa.z", 99)) { // The '99' checks for any numeric tag type
            this.posA = new BlockPos(
                    nbt.getInt("zone_controller.posa.x"),
                    nbt.getInt("zone_controller.posa.y"),
                    nbt.getInt("zone_controller.posa.z"));
        }

        if (nbt.contains("zone_controller.posb.x", 99) ||
                nbt.contains("zone_controller.posb.y", 99) ||
                nbt.contains("zone_controller.posb.z", 99)) { // The '99' checks for any numeric tag type
            this.posB = new BlockPos(
                    nbt.getInt("zone_controller.posb.x"),
                    nbt.getInt("zone_controller.posb.y"),
                    nbt.getInt("zone_controller.posb.z"));
        }

        if (nbt.contains("zone_controller.linked_map_controller")) {
            this.linkedMapController = NbtHelper.toBlockPos(nbt.getCompound("zone_controller.linked_map_controller"));
        }

        NbtList windowsList = nbt.getList("zone_controller.linked_windows", 10);
        linkedWindows = new ArrayList<>();
        for (int i = 0; i < windowsList.size(); i++) {
            linkedWindows.add(NbtHelper.toBlockPos(windowsList.getCompound(i)));
        }


        NbtList zonesChildList = nbt.getList("zone_controller.linked_child_zones", 10);
        linkedChildZoneControllers = new ArrayList<>();
        for (int i = 0; i < zonesChildList.size(); i++) {
            linkedChildZoneControllers.add(NbtHelper.toBlockPos(zonesChildList.getCompound(i)));
        }


        NbtList zonesParentList = nbt.getList("zone_controller.linked_parent_zones", 10);
        linkedParentZoneControllers = new ArrayList<>();
        for (int i = 0; i < zonesParentList.size(); i++) {
            linkedParentZoneControllers.add(NbtHelper.toBlockPos(zonesParentList.getCompound(i)));
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
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ZoneControllerScreenHandler(syncId, this);
    }

    private void unlinkMapController(World world){
        ModMessages.sendRemoveLinkPacket(world, getLinkedBlock(MapControllerBlockEntity.class));

        //Remove from MapController the ZoneController
        BlockEntity existingMapControllerBE = world.getBlockEntity(this.getLinkedBlock(MapControllerBlockEntity.class));
        if(existingMapControllerBE instanceof MapControllerBlockEntity)
            ((MapControllerBlockEntity) existingMapControllerBE).setLinkedBlock(null, ZoneControllerBlockEntity.class);

        setLinkedBlock(null, MapControllerBlockEntity.class);
    }

    private void unlinkSmallZombieDoorway(World world, BlockPos doorway){
        ModMessages.sendRemoveZoneLinkFromDoorwayPacket(world, doorway);

        //Remove from SmallZombieDoorway the zone
        BlockEntity doorwayBE = world.getBlockEntity(doorway);
        if (doorwayBE instanceof SmallZombieWindowBlockEntity)
            ((SmallZombieWindowBlockEntity) doorwayBE).setLinkedBlock(null, ZoneControllerBlockEntity.class);

        removeLinkedBlock(doorway, SmallZombieWindowBlockEntity.class);
    }

    /*private void unlinkZone(World world, BlockPos zonePos){
        ModMessages.sendRemoveZoneLinkFromZonePacket(world, getPos(), zonePos);

        //Remove from SmallZombieDoorway the zone
        BlockEntity zoneBE = world.getBlockEntity(zonePos);
        if (zoneBE instanceof ZoneControllerBlockEntity)
            ((ZoneControllerBlockEntity) zoneBE).setLinkedBlock(null, ZoneControllerBlockEntity.class);

        removeLinkedBlock(zonePos, ZoneControllerBlockEntity.class);
    }

    public BlockPos getLinkedParentZone() {
        return linkedParentZone;
    }

    public void setLinkedParentZone(BlockPos linkedParentZone) {
        if(this.linkedParentZone != null && world.getBlockEntity(this.linkedParentZone) instanceof  ZoneControllerBlockEntity)
            ModMessages.sendRemoveZoneLinkFromZonePacket(world, this.linkedParentZone, this.getPos());

        //Remove from parent's zone the zone
        if(this.linkedParentZone != null){
            BlockEntity zoneBE = world.getBlockEntity(this.linkedParentZone);
            if (zoneBE instanceof ZoneControllerBlockEntity)
                ((ZoneControllerBlockEntity) zoneBE).removeLinkedZone(this.getPos());
        }

        this.linkedParentZone = linkedParentZone;

        markDirty();
    }

    private List<BlockPos> getLinkedZone(){
        return new ArrayList<>(linkedZoneControllers);
    }

    private void removeLinkedZone(BlockPos zonePos){
        if(this.linkedZoneControllers.contains(zonePos))
            this.linkedZoneControllers.remove(zonePos);
    }

    private void addLinkedZone(BlockPos zonePos){
        if(!linkedZoneControllers.contains(zonePos))
            linkedZoneControllers.add(zonePos);
    }*/
    private List<BlockPos> getLinkedParentZoneControllers() {
        return new ArrayList<>(linkedParentZoneControllers);
    }

    private void removeLinkedParentZoneControllers(BlockPos doorwayPos) {
        if(this.linkedParentZoneControllers.contains(doorwayPos))
            this.linkedParentZoneControllers.remove(doorwayPos);
    }

    private void addLinkedParentZoneControllers(BlockPos doorwayPos) {
        if (!linkedParentZoneControllers.contains(doorwayPos))
            linkedParentZoneControllers.add(doorwayPos);
    }

    private List<BlockPos> getLinkedChildZoneControllers() {
        return new ArrayList<>(linkedChildZoneControllers);
    }

    private void removeLinkedChildZoneControllers(BlockPos doorwayPos) {
        if(this.linkedChildZoneControllers.contains(doorwayPos))
            this.linkedChildZoneControllers.remove(doorwayPos);
    }

    private void addLinkedChildZoneControllers(BlockPos doorwayPos) {
        if (!linkedChildZoneControllers.contains(doorwayPos))
            linkedChildZoneControllers.add(doorwayPos);
    }

    private List<BlockPos> getLinkedDoorways() {
        return new ArrayList<>(linkedWindows);
    }

    private void removeLinkedDoorway(BlockPos doorwayPos) {
        if(this.linkedWindows.contains(doorwayPos))
            this.linkedWindows.remove(doorwayPos);
    }

    private void addLinkedDoorway(BlockPos doorwayPos) {
        if (!linkedWindows.contains(doorwayPos))
            linkedWindows.add(doorwayPos);
    }

    public void addParent(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType){
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType)){
            addLinkedParentZoneControllers(linkedBlockPos);
        }

        markDirty();
    }

    public void removeParent(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType){
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType)){
            removeLinkedParentZoneControllers(linkedBlockPos);
        }

        markDirty();
    }

    public List<BlockPos> getParent(Class<? extends BlockEntity> linkType){
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType) && linkedParentZoneControllers != null){
            return getLinkedParentZoneControllers();
        }

        return null;
    }

    public void addChild(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType){
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType)){
            addLinkedChildZoneControllers(linkedBlockPos);
        }

        markDirty();
    }

    public void removeChild(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType){
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType)){
            removeLinkedChildZoneControllers(linkedBlockPos);
        }

        markDirty();
    }

    public List<BlockPos> getChild(Class<? extends BlockEntity> linkType){
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType) && linkedChildZoneControllers != null){
            return getLinkedChildZoneControllers();
        }

        return null;
    }

    @Override
    public void unlink(World world, Class<? extends BlockEntity> linkType) {
        if(MapControllerBlockEntity.class.isAssignableFrom(linkType) && this.linkedMapController != null){
            unlinkMapController(world);

        }else if(SmallZombieWindowBlockEntity.class.isAssignableFrom(linkType) && this.getLinkedDoorways() != null) {
            List<BlockPos> doorways = this.getLinkedDoorways();
            for (BlockPos doorway : doorways)
                unlinkSmallZombieDoorway(world, doorway);
        }
    }

    @Override
    public void setLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        if(MapControllerBlockEntity.class.isAssignableFrom(linkType))
            this.linkedMapController = linkedBlockPos;

        markDirty();
    }

    @Override
    public @Nullable BlockPos getLinkedBlock(Class<? extends BlockEntity> linkType) {
        if(MapControllerBlockEntity.class.isAssignableFrom(linkType))
            return this.linkedMapController;
        return null;
    }

    @Override
    public void addLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        if(SmallZombieWindowBlockEntity.class.isAssignableFrom(linkType))
            addLinkedDoorway(linkedBlockPos);

        markDirty();
    }

    @Override
    public void removeLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        if(SmallZombieWindowBlockEntity.class.isAssignableFrom(linkType))
            removeLinkedDoorway(linkedBlockPos);

        markDirty();
    }

    @Override
    public List<BlockPos> getAllLinkedBlocks(Class<? extends BlockEntity> linkType) {
        if(SmallZombieWindowBlockEntity.class.isAssignableFrom(linkType) && this.linkedWindows != null)
            return this.getLinkedDoorways();

        ZMCMod.LOGGER.warn("getAllLinkedBlocks from ZoneController called with wrong type : " + linkType);
        return null;
    }
}