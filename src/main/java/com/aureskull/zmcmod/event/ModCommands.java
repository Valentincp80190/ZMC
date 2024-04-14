package com.aureskull.zmcmod.event;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.management.GamesManager;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.util.ChatMessages;
import com.aureskull.zmcmod.util.PlayerData;
import com.aureskull.zmcmod.util.StateSaverAndLoader;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.UUID;

import static net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.*;

public class ModCommands {
    private static final String PREFIX = "zmc";

    public static void registerCommands() {
        ZMCMod.LOGGER.info("Registering ModCommands for " + ZMCMod.MOD_ID);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(PREFIX)
                .then(buildReadyToPlayCommand())
                .then(buildInviteCommand())
                .then(buildJoinGameCommand())
                .then(buildLeaveGameCommand())
            );
        });
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildReadyToPlayCommand() {
        return CommandManager.literal("ready")
            .executes(context -> {//SERVER SIDE
                PlayerEntity player = context.getSource().getPlayer();
                PlayerData playerData = StateSaverAndLoader.getPlayerState(player);

                if(playerData.getGameUUID() != null && GamesManager.getInstance().getGame(playerData.getGameUUID()) != null){
                    playerData.setReady(true);
                    ChatMessages.sendPlayerReadyConfirmationMessage((ServerPlayerEntity) player);
                }else{
                    ChatMessages.sendPlayerNotSubscribedToGameMessage((ServerPlayerEntity) player);
                }

                return 1;
            });
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildInviteCommand() {
        return CommandManager.literal("invite")
            .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                .executes(context -> {
                    Collection<GameProfile> gameProfiles = GameProfileArgumentType.getProfileArgument(context, "player");
                    GameProfile invitedPlayerGameProfile = gameProfiles.iterator().next();

                    UUID playerUUID = invitedPlayerGameProfile.getId();
                    ServerPlayerEntity invitedPlayerEntity = context.getSource().getServer().getPlayerManager().getPlayer(playerUUID);

                    invitePlayer(context.getSource().getPlayer(), invitedPlayerEntity);

                    return 1;
                }));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildJoinGameCommand() {
        return CommandManager.literal("join")
            .then(CommandManager.argument("gameUUID", StringArgumentType.string())
                .executes(context -> {
                    String gameIdStr = StringArgumentType.getString(context, "gameUUID");

                    UUID gameUUID = UUID.fromString(gameIdStr);
                    if(addPlayerToGamePlayers(context.getSource().getServer(), context.getSource().getPlayer(), gameUUID)){
                        PlayerData playerState = StateSaverAndLoader.getPlayerState(context.getSource().getPlayer());

                        playerState.setGameUUID(gameUUID);
                        ZMCMod.LOGGER.info("Player is now playing on game id: " + (playerState.getGameUUID() != null  ? playerState.getGameUUID().toString() : "null"));
                    }
                    return 1;
                }));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildLeaveGameCommand() {
        return CommandManager.literal("leave")
            .executes(context -> {
                PlayerEntity player = context.getSource().getPlayer();
                PlayerData playerData = StateSaverAndLoader.getPlayerState(player);

                if(playerData != null){
                    if(playerData.getGameUUID() != null){
                        removePlayerFromGamePlayers(context.getSource().getServer(), (ServerPlayerEntity) player, playerData.getGameUUID());
                        playerData.setGameUUID(null);
                    }else{
                        ChatMessages.sendPlayerNotSubscribedToGameMessage((ServerPlayerEntity) player);
                    }
                }

                return 1;
            });
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
                                ChatMessages.sendGameSubscriptionConfirmationMessage(player, mapControllerBlockEntity.getMapName());
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

    private static void invitePlayer(PlayerEntity playerSender, PlayerEntity playerReceiver){
        PlayerData playerDataSender = StateSaverAndLoader.getPlayerState(playerSender);

        //Sender doesn't have an active game
        if(playerDataSender.getGameUUID() == null){
            Text message = Text.literal("You do not have an active game.")
                    .formatted(Formatting.DARK_RED);

            playerSender.sendMessage(ChatMessages.getPrefix().append(message));
            return;
        }

        //Receiver not found
        if(playerReceiver == null){
            Text message = Text.literal("Player not found.")
                    .formatted(Formatting.DARK_RED);
            playerSender.sendMessage(ChatMessages.getPrefix().append(message));
            return;
        }

        //Sender = Receiver
        if(playerReceiver.getUuid().toString().equals(playerSender.getUuid().toString()))
            return;


        GamesManager.GameInfo gameInfo = GamesManager.getInstance().getGame(playerDataSender.getGameUUID());

        if(gameInfo != null){
            ChatMessages.sendGameInviteMessage(((ServerPlayerEntity)  playerSender), ((ServerPlayerEntity) playerReceiver), playerDataSender.getGameUUID());

            Text message = Text.literal("Invitation sent to " + playerReceiver.getName().getString() + ".")
                    .formatted(Formatting.DARK_GREEN);
            playerSender.sendMessage(ChatMessages.getPrefix().append(message));
        }else{
            Text message = Text.literal("You do not have an active game.")
                    .formatted(Formatting.DARK_RED);
            playerSender.sendMessage(ChatMessages.getPrefix().append(message));
        }
    }


    public static String getPrefix() {
        return PREFIX;
    }
}
