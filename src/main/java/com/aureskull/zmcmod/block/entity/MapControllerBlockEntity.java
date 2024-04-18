package com.aureskull.zmcmod.block.entity;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.ILinkable;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import com.aureskull.zmcmod.management.GamesManager;
import com.aureskull.zmcmod.management.GamePlayerManager;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.mapcontroller.MapControllerScreenHandler;
import com.aureskull.zmcmod.sound.ModSounds;
import com.aureskull.zmcmod.util.ChatMessages;
import com.aureskull.zmcmod.util.PlayerData;
import com.aureskull.zmcmod.util.StateSaverAndLoader;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
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
    private BlockPos posA = new BlockPos(pos.getX() - 64, pos.getY() - 10, pos.getZ() - 64);
    private BlockPos posB = new BlockPos(pos.getX() + 64, pos.getY() + 24, pos.getZ() + 64);

    private static final int MAX_ZOMBIES = 24;//TODO: A multiplier par le nombre de joueur
    private final int ROUND_ONE_ZOMBIES = 6;//TODO: Faire une classe GameRoundManager

    private static final int NEXT_ROUND_DELAY_TICKS = 220; // 11 seconds in ticks

    private String mapName = "";
    public UUID gameUUID;
    private boolean started = false;
    private int round = 0;
    private int zombiesRemainingInRound = 0;
    private int zombiesInRound;
    private int killedZombiesInRound = 0;
    private int spawnLuck = 7;//TODO: Faire une classe GameSpawnManager
    private int roundStartDelay = 0;
    private boolean canStartRound = false;
    private boolean roundStarted = false;

    public static final Map<UUID, BlockPos> playerCurrentZone = new HashMap<>();

    private BlockPos linkedZoneController = null;

    private GamePlayerManager playerManager = new GamePlayerManager(this);

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

        nbt.putInt("map_controller.zombies_remaining_in_round", this.zombiesRemainingInRound);
        nbt.putInt("map_controller.zombies_in_round", this.zombiesInRound);
        nbt.putInt("map_controller.killed_zombies_in_round", this.killedZombiesInRound);

        nbt.putBoolean("map_controller.started", this.started);
        nbt.put("map_controller.position_a", NbtHelper.fromBlockPos(posA));
        nbt.put("map_controller.position_b", NbtHelper.fromBlockPos(posB));

        if(this.gameUUID != null)
            nbt.putUuid("map_controller.game_uuid", this.gameUUID);

        if (nbt.contains("map_controller.linked_zone_controller")) {
            this.linkedZoneController = NbtHelper.toBlockPos(nbt.getCompound("map_controller.linked_zone_controller"));
        }

        if (linkedZoneController != null) {
            nbt.put("map_controller.linked_zone_controller", NbtHelper.fromBlockPos(linkedZoneController));
        }

        nbt.put("map_controller.subscribed_players", playerManager.writeSubscribedPlayersToNbt());
        //ZMCMod.LOGGER.info("Writing Subscribed Players to NBT: " + playerManager.getSubscribedPlayers());

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

        if (nbt.contains("map_controller.zombies_remaining_in_round"))
            this.zombiesRemainingInRound = nbt.getInt("map_controller.zombies_remaining_in_round");

        if (nbt.contains("map_controller.zombies_in_round"))
            this.zombiesInRound = nbt.getInt("map_controller.zombies_in_round");

        if (nbt.contains("map_controller.killed_zombies_in_round"))
            this.killedZombiesInRound = nbt.getInt("map_controller.killed_zombies_in_round");

        if (nbt.contains("map_controller.started"))
            this.started = nbt.getBoolean("map_controller.started");

        if (nbt.contains("map_controller.position_a")) {
            this.posA = NbtHelper.toBlockPos(nbt.getCompound("map_controller.position_a"));
        }

        if (nbt.contains("map_controller.position_b")) {
            this.posB = NbtHelper.toBlockPos(nbt.getCompound("map_controller.position_b"));
        }

        if (nbt.contains("map_controller.game_uuid"))
            this.gameUUID = nbt.getUuid("map_controller.game_uuid");

        if (nbt.contains("map_controller.linked_zone_controller")) {
            this.linkedZoneController = NbtHelper.toBlockPos(nbt.getCompound("map_controller.linked_zone_controller"));
        }

        if (nbt.contains("map_controller.subscribed_players", 9)) {
            NbtList uuidList = nbt.getList("map_controller.subscribed_players", 10); // 10 is the tag type for compound
            playerManager.readSubscribedPlayersFromNbt(uuidList);
            //ZMCMod.LOGGER.info("Subscribed players after reading from NBT: " + playerManager.getSubscribedPlayers());
        }

        if(started && gameUUID != null && getWorld() != null)
            GamesManager.getInstance().addGame(gameUUID, getPos(), getWorld().getRegistryKey());
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
        if(world.isClient){
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            // Check if the client instance and the player are not null
            if (minecraftClient != null && minecraftClient.player != null) {
                PlayerEntity player = minecraftClient.player;

                if(playerManager.getSubscribedPlayers().contains(player.getUuid())){
                    if(started && getBox().contains(player.getPos().x, player.getPos().y, player.getPos().z)){
                        PlayerData.displayHUD = true;
                    }else PlayerData.displayHUD = false;
                }
            }
        }else{
            if (!started) return;

            //Stop the game if there is no players
            if(playerManager.getSubscribedPlayers().size() == 0){
                setStart(false, null);
                return;
            }

            //If all the players are ready
            if(round == 0 && !playerManager.areAllSubscribedPlayersReady()) return;

            //Teleport all the players when all are ready
            if(round == 0){
                teleportAllPlayerInFirstZone();
                playerManager.resetPlayerMoney();
            }

            //Pause if none players connected
            //Pay attention from the fact that your UUID change everytime in dev
            if(playerManager.getConnectedSubscribedPlayers().size() == 0) return;

            manageRounds();

            if (roundStarted && getAllZombies().size() < MAX_ZOMBIES) {
                spawnZombie();
            }
        }
        //ZMCMod.LOGGER.info(mapName + ": " + playerManager.getSubscribedPlayers().toString());
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
        return killedZombiesInRound == zombiesInRound && roundStarted;
    }

    private void prepareForNextRound() {
        endRound();
        killAllZombies();
        roundStartDelay = NEXT_ROUND_DELAY_TICKS;

        //Update player HUD for animation
        for(PlayerEntity player : playerManager.getConnectedSubscribedPlayers())
            ModMessages.sendUpdateRoundHUDPacket(((ServerPlayerEntity) player), getRound()+1);
    }

    private void killAllZombies() {
        // Iterate through all entities in the world
        for (StandingZombieEntity entity : getAllZombies()) {

            BlockPos entityPos = entity.getMapControllerBlockPos();
            if (entityPos.getX() == getPos().getX() &&
                entityPos.getY() == getPos().getY() &&
                entityPos.getZ() == getPos().getZ()) {
                // Kills the zombie
                entity.kill();
            }
        }
    }

    private List<StandingZombieEntity> getAllZombies(){
        ServerWorld serverWorld = (ServerWorld) world;

        return serverWorld.getEntitiesByClass(
                StandingZombieEntity.class,
                new Box(this.posA.getX(), this.posA.getY(), this.posA.getZ(), this.posB.getX(), this.posB.getY(), this.posB.getZ()),
                (entity) -> true);
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

        if(!world.isClient()) {
            killAllZombies();
            if (p_started)
                startZombieMap(playerEntity);
            else {
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

    public int getKilledZombiesInRound() {
        return killedZombiesInRound;
    }

    public void setKilledZombiesInRound(int killedZombiesInRound) {
        this.killedZombiesInRound = killedZombiesInRound;
        markDirty();
        ZMCMod.LOGGER.info(killedZombiesInRound + "/" + zombiesInRound);
    }

    //Press button start -> Create a room -> (If each players are ready) -> Start map
    private void startZombieMap(ServerPlayerEntity player){
        if (!world.isClient()) {

            if(isMapAvailable(player)){
                ZMCMod.LOGGER.info("Starting Zombie Map on Server...");

                if(gameUUID != null)
                    GamesManager.getInstance().removeGame(gameUUID);
                playerManager = new GamePlayerManager(this);

                generateGameUUID();
                GamesManager.getInstance().addGame(gameUUID, getPos(), getWorld().getRegistryKey());
                subscribePlayer(player.getUuid());
                ChatMessages.sendGameSubscriptionConfirmationMessage(player, mapName);

                roundStartDelay = 40;
            }else{
                this.setStart(false, null);
            }
        }
    }

    private void generateGameUUID(){
        gameUUID = UUID.randomUUID();
        markDirty();
    }

    private void teleportAllPlayerInFirstZone() {
        if (!(world instanceof ServerWorld)) {
            return; // Exit if not on the server side.
        }

        ServerWorld serverWorld = (ServerWorld) world;

        ZoneControllerBlockEntity zoneControllerBlockEntity = ((ZoneControllerBlockEntity) world.getBlockEntity(this.linkedZoneController));

        BlockPos spawnPoint = zoneControllerBlockEntity.getSpawnPoint();
        double spawnX = spawnPoint.getX() + 0.5;
        double spawnY = spawnPoint.getY();
        double spawnZ = spawnPoint.getZ() + 0.5;

        for (PlayerEntity player : playerManager.getConnectedSubscribedPlayers()) {
            ((ServerPlayerEntity) player).teleport(serverWorld, spawnX, spawnY, spawnZ, player.getYaw(), player.getPitch());
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
    private void resetRoundState() {
        setZombiesInRound(calculateZombiesInRound());
        setKilledZombiesInRound(0);
        setSpawnLuck(calculateSpawnLuck());
    }

    private int calculateZombiesInRound() {
        // Implement the formula for calculating the number of zombies per round
        //return (int) ((this.getRound() * 5) + ((this.getRound() * 5) * 0.25 * (1 - 1)));
        float multiplier = getRound() / 5;
        if(multiplier < 1)
            multiplier = (float)1.0;
        else if(getRound() >= 10)
            multiplier *= (getRound() * 0.15);

        int connectedPlayers = playerManager.getConnectedSubscribedPlayers().size();

        int temp = -1;
        if(connectedPlayers == 1)
            temp = (int) (MAX_ZOMBIES + (0.5 * ROUND_ONE_ZOMBIES * multiplier));
        else if(connectedPlayers > 1){
            temp = (int) (MAX_ZOMBIES + ((connectedPlayers - 1) * ROUND_ONE_ZOMBIES * multiplier));
        }

        if(getRound() < 2){
            return (int)(temp * 0.25);
        }else if(getRound() < 3){
            return (int)(temp * 0.3);
        }else if(getRound() < 4){
            return (int)(temp * 0.5);
        }else if(getRound() < 5){
            return (int)(temp * 0.7);
        }else if(getRound() < 6){
            return (int)(temp * 0.9);
        }

        return temp;
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

        //Update player HUD for animation
        if(getRound() == 1){
            for(PlayerEntity player : playerManager.getConnectedSubscribedPlayers())
                ModMessages.sendUpdateRoundHUDPacket(((ServerPlayerEntity) player), getRound());
        }
    }

    private void endRound() {
        roundStarted = false;
        playEndRoundSoundForPlayers();
    }

    private void playEndRoundSoundForPlayers() {
        for (UUID playerUUID : playerManager.getSubscribedPlayers()) {
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
        this.zombiesRemainingInRound = zombiesInRound;
        markDirty();
    }

    private void broadcastRoundUpdate() {
        if (!(world instanceof ServerWorld)) return;

        ServerWorld serverWorld = (ServerWorld) world;
        PacketByteBuf buffer = createRoundUpdatePacket();
        buffer.writeInt(round);

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

        unsubscribeAllPlayer();
        GamesManager.getInstance().removeGame(this.gameUUID);
    }

    private void spawnZombie(){
        Random random = new Random();
        int luck = random.nextInt(1000);

        //Map started => SpawnZombie if we doesn't exceed the number of zombie on the map
        //Normally chance < 7
        if(luck < spawnLuck && this.zombiesRemainingInRound > 0) {

            ZoneControllerBlockEntity zone = getRandomZoneOccupiedByPlayer();
            if(zone != null &&
                zone.getAllLinkedBlocks(SmallZombieWindowBlockEntity.class).size() > 0){
                try{
                    zone.spawnZombie(true);
                    zombiesRemainingInRound--;
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

    public GamePlayerManager getPlayerManager() {
        return playerManager;
    }

    public void subscribePlayer(UUID playerUuid) {
        playerManager.subscribePlayer(playerUuid);

        MinecraftServer server = getWorld().getServer();
        PlayerData playerData = StateSaverAndLoader.getPlayerState(server.getPlayerManager().getPlayer(playerUuid));
        playerData.setGameUUID(gameUUID);

        markDirty();
    }

    public void unsubscribePlayer(UUID playerUuid) {
        playerManager.unsubscribePlayer(playerUuid);
        markDirty();
    }

    public void unsubscribeAllPlayer(){
        List<UUID> subscribedPlayers = playerManager.getSubscribedPlayers();
        MinecraftServer server = getWorld().getServer();

        for(UUID playerUUID : subscribedPlayers){
            try {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUUID);
                if(player != null){//Player is connected
                    PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                    playerData.setGameUUID(null);
                }
            }catch (Exception e){
                ZMCMod.LOGGER.error(e.getMessage(), e);
            }
        }

        playerManager.clearSubscribedPlayers();
        markDirty();
    }

    public BlockPos getPosA() {
        return posA;
    }

    public BlockPos getPosB() {
        return posB;
    }

    public void setPosA(BlockPos posA) {
        this.posA = posA;
    }

    public void setPosB(BlockPos posB) {
        this.posB = posB;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
