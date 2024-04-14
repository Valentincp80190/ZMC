package com.aureskull.zmcmod.util;

import java.util.UUID;

public class PlayerData {
    private static UUID gameUUID;
    private boolean ready;

    //NOT NBT
    public static boolean displayHUD = false;

    public static UUID getGameUUID() {
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
