package com.aureskull.zmcmod.util;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.management.GamesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;


public class PlayerHelper {
    public static boolean isPlaying(PlayerEntity player, MapControllerBlockEntity mapControllerBlockEntity){
        if (player.getWorld().isClient() || !(player.getWorld() instanceof ServerWorld)){
            ZMCMod.LOGGER.error("Client cannot call isPlaying(PlayerEntity, MapControllerBlockEntity).");
            return false;
        }

        if (player == null || player.getWorld() == null) {
            ZMCMod.LOGGER.warn("Player or player world cannot be null.");
            return false;
        }

        if(mapControllerBlockEntity == null){
            ZMCMod.LOGGER.warn("MapControllerBlockEntity cannot be null.");
            return false;
        }

        if(mapControllerBlockEntity.isStarted()){
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
            if(playerData.getGameUUID() != null &&
                    playerData.getGameUUID() == mapControllerBlockEntity.gameUUID){
                if(mapControllerBlockEntity.getPlayerManager().isSubscribedPlayer(player.getUuid())){
                    //If the player is inside the mapController zone => Player is playing
                    Box mapControllerBox = mapControllerBlockEntity.getBox();
                    if(mapControllerBox.contains(player.getPos().x, player.getPos().y, player.getPos().z))
                        return true;
                }
            }
        }

        return false;
    }

    public static boolean isPlaying(PlayerEntity player){
        if (player == null || player.getWorld() == null) {
            ZMCMod.LOGGER.warn("Player or player world cannot be null.");
            return false;
        }

        if (!player.getWorld().isClient() && player.getWorld() instanceof ServerWorld world) {
            //Server
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
            if(playerData.getGameUUID() != null){
                GamesManager.GameInfo gameInfo = GamesManager.getInstance().getGame(playerData.getGameUUID());

                if(gameInfo != null){
                    if(world.getBlockEntity(gameInfo.getBlockPos()) instanceof MapControllerBlockEntity mapControllerBlockEntity){
                        if(mapControllerBlockEntity.isStarted()){
                            if(playerData.getGameUUID() == mapControllerBlockEntity.gameUUID){
                                if(mapControllerBlockEntity.getPlayerManager().isSubscribedPlayer(player.getUuid())){
                                    //If the player is inside the mapController zone => Player is playing
                                    Box mapControllerBox = mapControllerBlockEntity.getBox();
                                    if(mapControllerBox.contains(player.getPos().x, player.getPos().y, player.getPos().z))
                                        return true;
                                }
                            }
                        }
                    }
                }
            }

        } else {
            //Client
            if(PlayerData.getGameUUID() != null){
                GamesManager.GameInfo gameInfo = GamesManager.getInstance().getGame(PlayerData.getGameUUID());

                if(gameInfo != null){
                    if(player.getWorld().getBlockEntity(gameInfo.getBlockPos()) instanceof MapControllerBlockEntity mapControllerBlockEntity){
                        if(mapControllerBlockEntity.getPlayerManager().isSubscribedPlayer(player.getUuid())){
                            //If the player is inside the mapController zone => Player is playing
                            Box mapControllerBox = mapControllerBlockEntity.getBox();
                            if(mapControllerBox.contains(player.getPos().x, player.getPos().y, player.getPos().z))
                                return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
