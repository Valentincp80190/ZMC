package com.aureskull.zmcmod.util;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class PlayerData {
    private UUID gameUUID;
    private boolean ready;

    public UUID getGameUUID() {
        return gameUUID;
    }

    public void setGameUUID(UUID gameUUID) {
        this.gameUUID = gameUUID;
        this.ready = false;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
