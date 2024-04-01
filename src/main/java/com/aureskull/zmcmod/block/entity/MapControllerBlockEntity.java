package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ILinkable;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.mapcontroller.MapControllerScreenHandler;
import com.aureskull.zmcmod.sound.ModSounds;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MapControllerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ILinkable {
    private static final int MAX_ZOMBIES = 24;
    private static final int NEXT_ROUND_DELAY_TICKS = 220; // 11 seconds in ticks

    public String mapName = "";
    private boolean started = false;
    private int round = 0;
    private int zombiesInRound = 0;
    private int killedZombiesInRound = 0;
    private int spawnLuck = 7;
    private int roundStartDelay = 0;
    private boolean canStartRound = false;
    private boolean roundStarted = false;

    public static final Map<UUID, BlockPos> playerCurrentZone = new HashMap<>();

    private BlockPos linkedZoneController = null;

    public MapControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAP_CONTROLLER_BLOCK_ENTITY, pos, state);
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
        return Text.literal("Map Controller");
    }

    @Override
    protected void writeNbt(NbtCompound nbt){
        nbt.putString("map_controller.mapname", this.mapName);
        nbt.putInt("map_controller.round", this.round);
        nbt.putBoolean("map_controller.round_started", this.roundStarted);
        nbt.putInt("map_controller.zombies_in_round", this.zombiesInRound);
        nbt.putInt("map_controller.killed_zombies_in_round", this.killedZombiesInRound);
        nbt.putBoolean("map_controller.started", this.started);

        if (nbt.contains("map_controller.linked_zone_controller")) {
            this.linkedZoneController = NbtHelper.toBlockPos(nbt.getCompound("map_controller.linked_zone_controller"));
        }

        if (linkedZoneController != null) {
            nbt.put("map_controller.linked_zone_controller", NbtHelper.fromBlockPos(linkedZoneController));
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        if (nbt.contains("map_controller.mapname"))
            this.mapName = nbt.getString("map_controller.mapname");

        if (nbt.contains("map_controller.round"))
            this.round = nbt.getInt("map_controller.round");

        if (nbt.contains("map_controller.round_started"))
            this.roundStarted = nbt.getBoolean("map_controller.round_started");

        if (nbt.contains("map_controller.zombies_in_round"))
            this.zombiesInRound = nbt.getInt("map_controller.zombies_in_round");

        if (nbt.contains("map_controller.killed_zombies_in_round"))
            this.killedZombiesInRound = nbt.getInt("map_controller.killed_zombies_in_round");

        if (nbt.contains("map_controller.started"))
            this.started = nbt.getBoolean("map_controller.started");

        if (nbt.contains("map_controller.linked_zone_controller")) {
            this.linkedZoneController = NbtHelper.toBlockPos(nbt.getCompound("map_controller.linked_zone_controller"));
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
        if (world.isClient) return;

        if (!started) return;

        manageRounds();
        if (roundStarted) {
            spawnZombie();
        }
    }

    private void manageRounds() {
        if (haveAllZombiesBeenKilled()) {
            prepareForNextRound();

        } else if (roundStartDelay <= 0 && !roundStarted) {
            canStartRound = true;
        } else if (roundStartDelay > 0) {
            roundStartDelay--;
        }

        if (canStartRound) {
            goToNextRound();
        }
    }

    private boolean haveAllZombiesBeenKilled() {
        return killedZombiesInRound == getZombiesInRound() && roundStarted;
    }

    private void prepareForNextRound() {
        endRound();
        roundStartDelay = NEXT_ROUND_DELAY_TICKS;
    }



    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MapControllerScreenHandler(syncId, this);
    }

    @Override
    public void unlink(World world, Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType) && linkedZoneController != null){
            unlinkZone(world);
        }
    }

    private void unlinkZone(World world){
        ModMessages.sendRemoveLinkPacket(world, this.getLinkedBlock(ZoneControllerBlockEntity.class));

        //Remove from MapController the zone
        BlockEntity zoneControllerBE = world.getBlockEntity(this.getLinkedBlock(ZoneControllerBlockEntity.class));
        if(zoneControllerBE instanceof ZoneControllerBlockEntity)
            ((ZoneControllerBlockEntity) zoneControllerBE).setLinkedBlock(null, MapControllerBlockEntity.class);

        setLinkedBlock(null, ZoneControllerBlockEntity.class);
    }

    @Override
    public void setLinkedBlock(BlockPos linkedBlockPos, Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType)){
            this.linkedZoneController = linkedBlockPos;
        }
        markDirty();
    }

    @Override
    public @Nullable BlockPos getLinkedBlock(Class<? extends BlockEntity> linkType) {
        if(ZoneControllerBlockEntity.class.isAssignableFrom(linkType))
            return this.linkedZoneController;

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

    public boolean isStarted() {
        return started;
    }

    public void setStart(boolean p_started, ServerPlayerEntity playerEntity) {
        this.started = p_started;
        markDirty();
        ZMCMod.LOGGER.info("Started state changed to " + this.started);

        if(!world.isClient()){
            if(p_started)
                startZombieMap(playerEntity);
            else{
                stopZombieMap();
            }
        }
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
        markDirty();
    }

    private int getZombiesInRound(){
        return (int) ((this.getRound() * 5) + ((this.getRound() * 5) * 0.25 * (1-1)));
    }

    public int getKilledZombiesInRound() {
        return killedZombiesInRound;
    }

    public void setKilledZombiesInRound(int killedZombiesInRound) {
        this.killedZombiesInRound = killedZombiesInRound;
        markDirty();
        ZMCMod.LOGGER.info(this.killedZombiesInRound + "/" + getZombiesInRound());
    }


    private void startZombieMap(ServerPlayerEntity player){
        if (!world.isClient()) {

            if(isMapAvailable(player)){
                ZMCMod.LOGGER.info("Starting Zombie Map on Server...");
                teleportAllPlayerInFirstZone();
                roundStartDelay = 40;
                //goToNextRound();
            }else{
                this.setStart(false, null);
            }
        }
    }

    private void teleportAllPlayerInFirstZone() {
        ZoneControllerBlockEntity zoneControllerBlockEntity = ((ZoneControllerBlockEntity) world.getBlockEntity(this.linkedZoneController));

        BlockPos spawnPoint = zoneControllerBlockEntity.getSpawnPoint();
        double spawnX = spawnPoint.getX() + 0.5;
        double spawnY = spawnPoint.getY();
        double spawnZ = spawnPoint.getZ() + 0.5;

        for (PlayerEntity player : world.getPlayers()) {
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

                // Teleport each server player
                serverPlayer.teleport((ServerWorld) world, spawnX, spawnY, spawnZ, player.getYaw(), player.getPitch());
            }
        }
    }

    private boolean isMapAvailable(ServerPlayerEntity player){
        try {
            if(this.linkedZoneController == null
                || !(world.getBlockEntity(this.linkedZoneController) instanceof ZoneControllerBlockEntity)) {
                try{
                    player.sendMessage(Text.literal("Warning : Please connect a Zone Controller to the Map Controller to start the game.").formatted(Formatting.GOLD), false); // Send the message to all players
                }catch (Exception e){
                    ZMCMod.LOGGER.error(e.getMessage() + e.getStackTrace());
                }

                return false;
            }

            return true;
        }catch (Exception e){
            ZMCMod.LOGGER.error(e.getMessage() + e.getStackTrace());
            return false;
        }
    }

    /*private void goToNextRound(){
        if (!world.isClient()) {
            setRound(this.getRound() + 1);
            ZMCMod.LOGGER.info("Round is: " + this.round);
            setZombiesInRound((int) ((this.getRound() * 5) + ((this.getRound() * 5) * 0.25 * (1-1))));
            setKilledZombiesInRound(0);
            setSpawnLuck(20 + ((int) ( ((getRound()-1) * .6) > 60 ? 60 : (getRound()-1) * .6)));
            roundStarted = true;

            broadcastRoundUpdate();
        }
    }*/

    private void resetRoundState() {
        setZombiesInRound(calculateZombiesInRound());
        setKilledZombiesInRound(0);
        setSpawnLuck(calculateSpawnLuck());
    }

    private int calculateZombiesInRound() {
        // Implement the formula for calculating the number of zombies per round
        return (int) ((this.getRound() * 5) + ((this.getRound() * 5) * 0.25 * (1 - 1)));
    }

    private int calculateSpawnLuck() {
        // Implement the formula for calculating spawn luck
        return 20 + Math.min(60, ((getRound() - 1) * 6));
    }

    private void goToNextRound() {
        setRound(getRound() + 1);
        resetRoundState();
        ZMCMod.LOGGER.info("Round is: " + this.round);
        roundStarted = true;
        canStartRound = false;
        broadcastRoundUpdate();
    }

    /*private void endRound(){
        canStartRound = false;
        roundStarted = false;

        for (Map.Entry<UUID, BlockPos> entry : playerCurrentZone.entrySet()){
            if(world.getPlayerByUuid(entry.getKey()) != null){
                PlayerEntity player = world.getPlayerByUuid(entry.getKey());
                player.playSound(ModSounds.ROUND_END, SoundCategory.AMBIENT, 0.5f, 1.0f);
            }else{
                playerCurrentZone.remove(entry.getKey());
            }
        }
    }*/

    private void endRound() {
        roundStarted = false;
        playEndRoundSoundForPlayers();
    }

    private void playEndRoundSoundForPlayers() {
        for (UUID playerUUID : playerCurrentZone.keySet()) {
            PlayerEntity player = world.getPlayerByUuid(playerUUID);
            if (player == null) {
                playerCurrentZone.remove(playerUUID);
                continue;
            }
            player.playSound(ModSounds.ROUND_END, SoundCategory.AMBIENT, 0.5f, 1.0f);
        }
    }

    public void setZombiesInRound(int zombiesInRound) {
        this.zombiesInRound = zombiesInRound;
        markDirty();
    }

    private void broadcastRoundUpdate() {
        if (!(world instanceof ServerWorld)) return;

        ServerWorld serverWorld = (ServerWorld) world;
        PacketByteBuf buffer = createRoundUpdatePacket();
        PlayerLookup.tracking(serverWorld, getPos()).forEach(player ->
                ServerPlayNetworking.send(player, ModMessages.MAP_CONTROLLER_UPDATE_ROUND, buffer)
        );
    }

    private PacketByteBuf createRoundUpdatePacket() {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBlockPos(getPos());
        buffer.writeInt(getRound());
        return buffer;
    }

    private void stopZombieMap(){
        this.setRound(0);
        this.roundStarted = false;
    }

    private void spawnZombie(){
        Random random = new Random();
        int luck = random.nextInt(1000);

        //Map started => SpawnZombie if we doesn't exceed the number of zombie on the map
        //Normaly chance < 7
        if(luck < spawnLuck && this.zombiesInRound > 0) {

            ZoneControllerBlockEntity zone = getRandomZoneOccupiedByPlayer();
            if(zone != null){
                try{
                    zone.spawnZombie(true);
                    zombiesInRound--;
                }catch (Exception e){
                    ZMCMod.LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    private ZoneControllerBlockEntity getRandomZoneOccupiedByPlayer(){
        //While there is a player like 'on the map'
        while(playerCurrentZone.size() != 0){
            Random random = new Random();
            int randomPlayerIndex = random.nextInt(playerCurrentZone.size());

            ArrayList<UUID> playerUUIDs = new ArrayList<>();
            for (Map.Entry<UUID, BlockPos> entry : playerCurrentZone.entrySet())
                playerUUIDs.add(entry.getKey());

            UUID randomPlayerUUID = playerUUIDs.get(randomPlayerIndex);
            BlockPos zoneBlockPos = playerCurrentZone.get(randomPlayerUUID);

            if(!isPlayerInsideItsZone(randomPlayerUUID)){
                playerCurrentZone.remove(randomPlayerUUID);
            }else{
                return ((ZoneControllerBlockEntity) world.getBlockEntity(zoneBlockPos));
            }
        }
        return null;
    }

    private void setSpawnLuck(int spawnLuck){
        this.spawnLuck = spawnLuck;
    }

    private boolean isPlayerInsideItsZone(UUID playerUUID){
        // Check if the world is a ServerWorld instance
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;

            // Attempt to retrieve the player by UUID
            PlayerEntity player = serverWorld.getPlayerByUuid(playerUUID);

            if (player != null) {
                // If the player is found, check the zone
                BlockPos bp = playerCurrentZone.get(playerUUID);
                if (bp != null && world.getBlockEntity(bp) instanceof ZoneControllerBlockEntity zoneControllerBE) {
                    //If the ZoneController is connected to the current MapController
                    BlockPos mapControllerBlockPos = zoneControllerBE.findMapControllerRecursively(zoneControllerBE, new ArrayList<BlockPos>());
                    if(mapControllerBlockPos != null &&
                            world.getBlockEntity(mapControllerBlockPos) instanceof MapControllerBlockEntity mapControllerBlockEntity
                            && mapControllerBlockEntity.getPos() == this.getPos()){

                        Box zoneBox = zoneControllerBE.getBox();

                        // Use the player's current position to check if they're inside the box
                        return zoneBox.contains(player.getPos().x, player.getPos().y, player.getPos().z);
                    }
                }
            }
        }
        return false;
    }
}
