package com.aureskull.zmcmod.util;

import java.util.UUID;

public class PlayerData {
    private static UUID gameUUID;
    private boolean ready;

    private static int money;

    //NOT NBT
    public static boolean displayHUD = false;

    public static UUID getGameUUID() {
        return gameUUID;
    }

    public void setGameUUID(UUID gameUUID) {
        this.gameUUID = gameUUID;
        ready = false;
        money = 500;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public static int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void addMoney(int amount){
        money += amount;
    }
}
