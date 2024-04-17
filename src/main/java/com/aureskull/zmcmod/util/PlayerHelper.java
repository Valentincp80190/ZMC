package com.aureskull.zmcmod.util;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.management.GamesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;


public class PlayerHelper {

    public static boolean isInHisMapControllerArea(PlayerEntity player){
        if (player == null || player.getWorld() == null) {
            ZMCMod.LOGGER.warn("Player or player world cannot be null.");
            return false;
        }

        if (!player.getWorld().isClient() && player.getWorld() instanceof ServerWorld world) {
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
            if(playerData.getGameUUID() != null){
                GamesManager.GameInfo gameInfo = GamesManager.getInstance().getGame(playerData.getGameUUID());

                if(gameInfo != null){
                    if(world.getBlockEntity(gameInfo.getBlockPos()) instanceof MapControllerBlockEntity mapControllerBlockEntity){
                        Box mapControllerBox = mapControllerBlockEntity.getBox();
                        if(mapControllerBox.contains(player.getPos().x, player.getPos().y, player.getPos().z))
                            return true;
                    }
                }
            }
            return false;


        } else {
            if(PlayerData.getGameUUID() != null){
                GamesManager.GameInfo gameInfo = GamesManager.getInstance().getGame(PlayerData.getGameUUID());

                if(gameInfo != null){
                    if(player.getWorld().getBlockEntity(gameInfo.getBlockPos()) instanceof MapControllerBlockEntity mapControllerBlockEntity){
                        Box mapControllerBox = mapControllerBlockEntity.getBox();
                        if(mapControllerBox.contains(player.getPos().x, player.getPos().y, player.getPos().z))
                            return true;
                    }
                }
            }
            return false;
        }
    }
}
