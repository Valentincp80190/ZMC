package com.aureskull.zmcmod.block;

import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.sound.ModSounds;
import com.aureskull.zmcmod.util.PlayerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBuyable {
    default boolean canAfford(PlayerEntity player, int amount) {
        return PlayerHelper.getMoney(player) >= amount;
    }

    default void buy(World world, PlayerEntity player, int amount) {
        if(canAfford(player, amount)){
            buyEvent(player);
            PlayerHelper.addMoney(player, -amount);
            world.playSound(null, player.getBlockPos(), ModSounds.PURCHASE_ACCEPT, SoundCategory.BLOCKS, 0.5f, 1.0f);
        }else{
            world.playSound(null, player.getBlockPos(), ModSounds.PURCHASE_DENY, SoundCategory.BLOCKS, 0.5f, 1.0f);
        }
    }

    void buyEvent(PlayerEntity player);
}
