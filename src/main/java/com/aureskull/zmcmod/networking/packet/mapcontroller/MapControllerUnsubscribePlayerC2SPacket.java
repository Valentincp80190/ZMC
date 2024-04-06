package com.aureskull.zmcmod.networking.packet.mapcontroller;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.management.GamesManager;
import com.aureskull.zmcmod.util.ChatMessages;
import com.aureskull.zmcmod.util.PlayerData;
import com.aureskull.zmcmod.util.StateSaverAndLoader;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import javax.swing.plaf.nimbus.State;
import java.util.UUID;

public class MapControllerUnsubscribePlayerC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        server.execute(() -> {
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
            if(playerData != null){
                if(playerData.gameUUID != null){
                    removePlayerFromGamePlayers(server, player, playerData.gameUUID);
                    resetPlayerGameUUID(playerData);
                }else{
                    ChatMessages.sendPlayerNotSubscribedToGameMessage(player);
                }
            }
        });
    }

    private static void resetPlayerGameUUID(PlayerData playerData){
        playerData.gameUUID = null;
    }

    private static void removePlayerFromGamePlayers(MinecraftServer server, ServerPlayerEntity player, UUID gameUUID){
        try{
            GamesManager.GameInfo infos = GamesManager.getInstance().getGame(gameUUID);

            if(infos != null){
                if(infos.getBlockPos() != null && infos.getWorld(server) != null){
                    World world = infos.getWorld(server);

                    if(world.getBlockEntity(infos.getBlockPos()) instanceof MapControllerBlockEntity mapControllerBlockEntity){
                        if(mapControllerBlockEntity.existSubscribedPlayer(player.getUuid())){
                            mapControllerBlockEntity.unsubscribePlayer(player.getUuid());
                            ChatMessages.sendGameUnsubscriptionConfirmationMessage(player);
                        }else{
                            ChatMessages.sendPlayerNotSubscribedToGameMessage(player);
                        }
                    }
                }
            }
        }catch (Exception e){
            ZMCMod.LOGGER.error("Unsubscription error : " + e.getMessage() + e.getStackTrace());
        }
    }
}
