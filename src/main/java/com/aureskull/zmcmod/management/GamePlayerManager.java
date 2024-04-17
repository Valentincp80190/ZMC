package com.aureskull.zmcmod.management;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.util.PlayerData;
import com.aureskull.zmcmod.util.StateSaverAndLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class GamePlayerManager {
    private final List<UUID> subscribedPlayers = new ArrayList<>();

    MapControllerBlockEntity mapControllerBlockEntity;

    public GamePlayerManager(MapControllerBlockEntity mapControllerBlockEntity) {
        this.mapControllerBlockEntity = mapControllerBlockEntity;
    }

    public void subscribePlayer(UUID playerUuid) {
        if (!subscribedPlayers.contains(playerUuid)) {
            subscribedPlayers.add(playerUuid);
        }
    }

    public void unsubscribePlayer(UUID playerUuid) {
        subscribedPlayers.remove(playerUuid);
    }

    public NbtList writeSubscribedPlayersToNbt() {
        NbtList uuidList = new NbtList();
        for (UUID uuid : subscribedPlayers) {
            NbtCompound uuidTag = new NbtCompound();
            uuidTag.putLong("MostBits", uuid.getMostSignificantBits());
            uuidTag.putLong("LeastBits", uuid.getLeastSignificantBits());
            uuidList.add(uuidTag);
        }
        return uuidList;
    }

    public void readSubscribedPlayersFromNbt(NbtList uuidList) {
        subscribedPlayers.clear();
        for (int i = 0; i < uuidList.size(); i++) {
            long mostBits = uuidList.getCompound(i).getLong("MostBits");
            long leastBits = uuidList.getCompound(i).getLong("LeastBits");
            UUID uuid = new UUID(mostBits, leastBits);

            subscribedPlayers.add(uuid);
        }
    }

    public boolean existSubscribedPlayer(UUID playerUuid){
        if(subscribedPlayers.contains(playerUuid))
            return true;
        return false;
    }

    public List<UUID> getSubscribedPlayers(){
        return subscribedPlayers;
    }

    public void clearSubscribedPlayers(){
        subscribedPlayers.clear();
    }

    public boolean areAllSubscribedPlayersReady(){
        MinecraftServer server = mapControllerBlockEntity.getWorld().getServer();

        for(UUID subscribedPlayerUUID : subscribedPlayers){
            PlayerEntity player = server.getPlayerManager().getPlayer(subscribedPlayerUUID);

            //If the disconnected players aren't ready, we consider just the connected players
            if(player == null) continue;

            PlayerData data = StateSaverAndLoader.getPlayerState(player);
            if(data.isReady()) continue;

            return false;
        }

        return true;
    }

    public List<PlayerEntity> getConnectedSubscribedPlayers(){
        List<PlayerEntity> connectedPlayers = new ArrayList<>();
        MinecraftServer server = mapControllerBlockEntity.getWorld().getServer();

        if (server == null) {
            ZMCMod.LOGGER.error("Server instance is null");
            return connectedPlayers;
        }

        try{
            Iterator<UUID> iterator = subscribedPlayers.iterator();
            while(iterator.hasNext()){
                UUID subscribedPlayerUUID = iterator.next();
                PlayerEntity player = server.getPlayerManager().getPlayer(subscribedPlayerUUID);
                if(player != null){
                    PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                    if(playerData.getGameUUID() != null && playerData.getGameUUID().equals(mapControllerBlockEntity.gameUUID)) {
                        connectedPlayers.add(player);
                    } else {
                        iterator.remove();
                    }
                }
            }
        }catch (Exception e){
            ZMCMod.LOGGER.error("An error occurred when recover the subscribed players : " + e.getMessage() + e.getStackTrace());
        }

        return connectedPlayers;
    }

    public void resetPlayerMoney() {
        for(PlayerEntity player : getConnectedSubscribedPlayers()){
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
            playerData.setMoney(500);
        }
    }
}
