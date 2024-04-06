package com.aureskull.zmcmod.networking.packet.mapcontroller;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.management.GamesManager;
import com.aureskull.zmcmod.util.ChatMessages;
import com.aureskull.zmcmod.util.PlayerData;
import com.aureskull.zmcmod.util.StateSaverAndLoader;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

public class MapControllerSubscribePlayerC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        UUID gameUUID = buf.readUuid();

        server.execute(() -> {
            if(addPlayerToGamePlayers(server, player, gameUUID))
                setPlayerGameUUID(player, gameUUID);
        });
    }

    private static void setPlayerGameUUID(ServerPlayerEntity player, UUID gameUUID){
        PlayerData playerState = StateSaverAndLoader.getPlayerState(player);

        playerState.gameUUID = gameUUID;
        ZMCMod.LOGGER.info("Player is now playing on game id: " + (playerState.gameUUID != null  ? playerState.gameUUID.toString() : "null"));
    }

    private static boolean addPlayerToGamePlayers(MinecraftServer server, ServerPlayerEntity player, UUID gameUUID){
        GamesManager.GameInfo infos = GamesManager.getInstance().getGame(gameUUID);

        if(infos != null){
            if(infos.getBlockPos() != null && infos.getWorld(server) != null){
                try{
                    World world = infos.getWorld(server);

                    if(world.getBlockEntity(infos.getBlockPos()) instanceof MapControllerBlockEntity mapControllerBlockEntity){
                        if(mapControllerBlockEntity.existSubscribedPlayer(player.getUuid())){
                            ChatMessages.sendAlreadyInGameMessage(player);
                        }else{
                            mapControllerBlockEntity.subscribePlayer(player.getUuid());
                            if(mapControllerBlockEntity.existSubscribedPlayer(player.getUuid()))
                                ChatMessages.sendGameSubscriptionConfirmationMessage(player, gameUUID, mapControllerBlockEntity.mapName);
                                return true;
                        }
                    }
                }catch (Exception e){
                    ZMCMod.LOGGER.error("Subscription error : " + e.getMessage() + e.getStackTrace());
                }
            }
        }
        return false;
    }
}
