package com.aureskull.zmcmod.util;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class PlayerData {
    public UUID gameUUID;

    public static UUID getGameUUID(ServerPlayerEntity player) {
        // Retrieve the player's game UUID from their NBT data or another data source
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
        return playerData.gameUUID == null ? null : playerData.gameUUID;
    }
}
