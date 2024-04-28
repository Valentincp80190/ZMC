package com.aureskull.zmcmod.block;

import com.aureskull.zmcmod.util.PlayerHelper;
import net.minecraft.entity.player.PlayerEntity;

public interface IBuyable {
    default boolean canAfford(PlayerEntity player, int amount) {
        return PlayerHelper.getMoney(player) >= amount;
    }

    default void buy(PlayerEntity player, int amount) {
        if(canAfford(player, amount)){
            buyEvent(player);
            PlayerHelper.addMoney(player, -amount);
        }
    }

    void buyEvent(PlayerEntity player);
}
