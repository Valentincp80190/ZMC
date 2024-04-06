package com.aureskull.zmcmod.management;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GamePlayerManager {
    private final List<UUID> subscribedPlayers = new ArrayList<>();

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
}
