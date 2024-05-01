package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ILinkable;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
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
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ZoneControllerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ILinkable {
    private BlockPos posA = new BlockPos(pos.getX() - 5, pos.getY() + 1, pos.getZ() - 5);
    private BlockPos posB = new BlockPos(pos.getX() + 5, pos.getY() + 5, pos.getZ() + 5);
    private BlockPos spawnPoint = new BlockPos(pos.getX(), pos.getY() + 2, pos.getZ());

    private float red = 1f;
    private float green = 1f;
    private float blue = 1f;

    private BlockPos linkedMapController = null;


    private List<BlockPos> linkedParentZoneControllers = new ArrayList<>();
    private List<BlockPos> linkedChildZoneControllers = new ArrayList<>();

    private List<BlockPos> linkedWindows = new ArrayList<>();

    private final int UPDATE_PLAYERS_ZONE_TIME = 20;//1 second
    private int last_update_players_zone_time = 0;

    private List<BlockPos> linkedDoors = new ArrayList<>();
    private boolean open = true;

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

        nbt.put("zone_controller.spawn_point", NbtHelper.fromBlockPos(spawnPoint));
        nbt.put("zone_controller.position_a", NbtHelper.fromBlockPos(posA));
        nbt.put("zone_controller.position_b", NbtHelper.fromBlockPos(posB));

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


        NbtList doorList = new NbtList();
        for (BlockPos pos : linkedDoors)
            doorList.add(NbtHelper.fromBlockPos(pos));
        if(doorList.size() > 0)
            nbt.put("zone_controller.linked_doors", doorList);

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

        if (nbt.contains("zone_controller.position_a")) {
            this.posA = NbtHelper.toBlockPos(nbt.getCompound("zone_controller.position_a"));
        }

        if (nbt.contains("zone_controller.position_b")) {
            this.posB = NbtHelper.toBlockPos(nbt.getCompound("zone_controller.position_b"));
        }

        if (nbt.contains("zone_controller.spawn_point")) {
            this.spawnPoint = NbtHelper.toBlockPos(nbt.getCompound("zone_controller.spawn_point"));
        }

        if (nbt.contains("zone_controller.spawn_point")) {
            this.spawnPoint = NbtHelper.toBlockPos(nbt.getCompound("zone_controller.spawn_point"));
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


        NbtList doorList = nbt.getList("zone_controller.linked_doors", 10);
        linkedDoors = new ArrayList<>();
        for (int i = 0; i < doorList.size(); i++) {
            linkedDoors.add(NbtHelper.toBlockPos(doorList.getCompound(i)));
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
        if(!world.isClient()) {

            if(last_update_players_zone_time == UPDATE_PLAYERS_ZONE_TIME) {
                updatePlayerZonePosition();
                last_update_players_zone_time = 0;
            }
            else last_update_players_zone_time++;

            return;
        }
    }

    private void updatePlayerZonePosition(){
        BlockPos mapControllerPos = findMapControllerRecursively(this, new ArrayList<BlockPos>());
        if(mapControllerPos != null && world.getBlockEntity(mapControllerPos) instanceof MapControllerBlockEntity mapControllerBlockEntity && mapControllerBlockEntity.isStarted()){
            // Create a box representing the zone
            Box zone = getBox();
            // Check for players within the zone
            List<PlayerEntity> playersInZone = world.getNonSpectatingEntities(PlayerEntity.class, zone);
            for(PlayerEntity player : playersInZone) {
                //player.sendMessage(Text.literal(player.getName().getString() + " has entered the zone at " + pos.toString()), true);
                mapControllerBlockEntity.playerCurrentZone.put(player.getUuid(), this.getPos());
            }
        }
    }

    public Box getBox(){
        // Calculate the minimum and maximum coordinates to define the zone
        int minX = Math.min(posA.getX(), posB.getX());
        int minY = Math.min(posA.getY(), posB.getY());
        int minZ = Math.min(posA.getZ(), posB.getZ());
        int maxX = Math.max(posA.getX(), posB.getX());
        int maxY = Math.max(posA.getY(), posB.getY());
        int maxZ = Math.max(posA.getZ(), posB.getZ());

        // Create a box representing the zone
        Box box = new Box(minX, minY, minZ, maxX+1, maxY+1, maxZ+1);

        return box;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ZoneControllerScreenHandler(syncId, this);
    }


    public BlockPos getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(BlockPos spawnPoint) {
        this.spawnPoint = spawnPoint;
        markDirty();
    }

    public void spawnZombie(boolean spawnInZoneOrInNeighborhood){
        //TODO: Lorsque les portes arriveront, vérifier si la salle voisine est ouverte ou non
        try{
            Random random = new Random();
            int randomInt = random.nextInt(100);

            if(randomInt >= 25
                //75% that the zombie spawn in this zone
                || (linkedParentZoneControllers.size() == 0  && linkedChildZoneControllers.size() == 0)
                || spawnInZoneOrInNeighborhood == false){

                SmallZombieWindowBlockEntity smallZombieWindowBlockEntity = ((SmallZombieWindowBlockEntity) world.getBlockEntity(linkedWindows.get(new Random().nextInt(linkedWindows.size()))));

                if(smallZombieWindowBlockEntity.getLink(ZombieSpawnerBlockEntity.class).size() == 0)
                    return;

                ZombieSpawnerBlockEntity zombieSpawnerBlockEntity = ((ZombieSpawnerBlockEntity) world.getBlockEntity(smallZombieWindowBlockEntity.getLink(ZombieSpawnerBlockEntity.class).get(0)));
                zombieSpawnerBlockEntity.spawnZombie();
            }else{
                //25% that the zombie spawn in the parent/child zone
                randomInt = random.nextInt(1);
                if(randomInt == 0 && linkedParentZoneControllers.size() != 0){
                    //Spawn in parent random zone
                    BlockPos randomParentZone = linkedParentZoneControllers.get(new Random().nextInt(linkedParentZoneControllers.size()));
                    ((ZoneControllerBlockEntity)world.getBlockEntity(randomParentZone)).spawnZombie(false);
                }else {
                    //Spawn in child random zone
                    BlockPos randomChildZone = linkedChildZoneControllers.get(new Random().nextInt(linkedChildZoneControllers.size()));
                    ((ZoneControllerBlockEntity)world.getBlockEntity(randomChildZone)).spawnZombie(false);
                }
            }
        }catch (Exception e){
            ZMCMod.LOGGER.error("ZoneController - Spawn Zombie :" + e.getMessage() + e.getStackTrace());
        }
    }

    public BlockPos findMapControllerRecursively(ZoneControllerBlockEntity zone, ArrayList<BlockPos> visitedBlockPos) {
        // If this zone is directly linked to a MapControllerBlockEntity, return its BlockPos
        List<BlockPos> mapControllerBP = zone.getLink(MapControllerBlockEntity.class);


        if (mapControllerBP != null && mapControllerBP.size() > 0)
            return mapControllerBP.get(0);


        // Otherwise, recursively search through all parent zones
        //ZMCMod.LOGGER.info("Parents in zone " + zone.getPos() + " are : " + zone.getParent(ZoneControllerBlockEntity.class));
        for (BlockPos parentZoneBP : zone.linkedParentZoneControllers) {
            if (world.getBlockEntity(parentZoneBP) instanceof ZoneControllerBlockEntity parentZoneBE) {
                //ZMCMod.LOGGER.info("looking for Map Controller in parent Zone" + parentZoneBE.getPos());

                //Avoid loop
                if(visitedBlockPos.contains(parentZoneBP)) continue;
                else visitedBlockPos.add(parentZoneBP);

                BlockPos foundBP = findMapControllerRecursively(parentZoneBE, visitedBlockPos);

                if (foundBP != null && world.getBlockEntity(foundBP) instanceof MapControllerBlockEntity) {
                    // If one of the parent zones (or their parents, recursively) is linked to a MapController, return its BlockPos
                    return foundBP;
                }
            }
        }

        // If no MapControllerBlockEntity is found in the hierarchy, return null
        return null;
    }

    public BlockPos getPosA() {
        return posA;
    }

    public void setPosA(BlockPos posA) {
        this.posA = posA;
    }

    public BlockPos getPosB() {
        return posB;
    }

    public void setPosB(BlockPos posB) {
        this.posB = posB;
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }

    public float getGreen() {
        return green;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    @Override
    public void setLink(List<BlockPos> blocks, Class<? extends BlockEntity> linkType) {
        if(MapControllerBlockEntity.class.isAssignableFrom(linkType))
            linkedMapController = blocks.size() > 0 ? blocks.get(0) : null;

        else if(DoorBlockEntity.class.isAssignableFrom(linkType))
            linkedDoors = blocks;

        else if(SmallZombieWindowBlockEntity.class.isAssignableFrom(linkType))
            linkedWindows = blocks;
    }

    @Override
    public List<BlockPos> getLink(Class<? extends BlockEntity> linkType) {
        List<BlockPos> blockPosList = new ArrayList<>();

        if(MapControllerBlockEntity.class.isAssignableFrom(linkType)){
            if(linkedMapController != null) blockPosList.add(linkedMapController);
            return blockPosList;
        }

        if(DoorBlockEntity.class.isAssignableFrom(linkType))
            return linkedDoors;

        if(SmallZombieWindowBlockEntity.class.isAssignableFrom(linkType))
            return linkedWindows;

        return null;
    }

    @Override
    public void setParentLink(List<BlockPos> parents, Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType))
            linkedParentZoneControllers = parents;
    }

    @Override
    public void setChildLink(List<BlockPos> children, Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType))
            linkedChildZoneControllers = children;
    }

    @Override
    public List<BlockPos> getParentLink(Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType))
            return linkedParentZoneControllers;

        return null;
    }

    @Override
    public List<BlockPos> getChildLink(Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType))
            return linkedChildZoneControllers;

        return null;
    }
}