package com.aureskull.zmcmod.event;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.util.ChatMessages;
import com.aureskull.zmcmod.util.PlayerData;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.UUID;

public class ModCommands {
    private static final String PREFIX = "zmc";

    public static void registerCommands() {
        ZMCMod.LOGGER.info("Registering ModCommands for " + ZMCMod.MOD_ID);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(PREFIX)
                    .then(buildInviteCommand())
                    .then(buildJoinGameCommand())
                    .then(buildLeaveGameCommand())
            );
        });
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildInviteCommand() {
        return CommandManager.literal("invite")
            .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                .executes(context -> {
                    Collection<GameProfile> gameProfiles = GameProfileArgumentType.getProfileArgument(context, "player");
                    GameProfile invitedPlayerGameProfile = gameProfiles.iterator().next();

                    UUID playerUUID = invitedPlayerGameProfile.getId();
                    ServerPlayerEntity playerEntity = context.getSource().getServer().getPlayerManager().getPlayer(playerUUID);

                    if (playerEntity != null) {
                        UUID gameUUID = PlayerData.getGameUUID(playerEntity);

                        if (gameUUID != null) {
                            // Process the invite with the retrieved game UUID
                            sendInvitePlayerGamePacketToServer(invitedPlayerGameProfile.getId(), gameUUID);
                        }else {
                            Text message = Text.literal("You do not have an active game.")
                                    .formatted(Formatting.DARK_RED);

                            context.getSource().sendError(ChatMessages.getPrefix().append(message));
                        }
                    } else {
                        Text message = Text.literal("Player " + invitedPlayerGameProfile.getName() + " not found.")
                                .formatted(Formatting.DARK_RED);

                        context.getSource().sendError(ChatMessages.getPrefix().append(message));
                    }
                    return 1;
                }));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildJoinGameCommand() {
        return CommandManager.literal("joinGame")
                .then(CommandManager.argument("gameUUID", StringArgumentType.string())
                        .executes(context -> {
                            String gameIdStr = StringArgumentType.getString(context, "gameUUID");

                            UUID gameUUID = UUID.fromString(gameIdStr);
                            sendJoinGamePacketToServer(gameUUID);

                            return 1;
                        }));
    }

    /*private static LiteralArgumentBuilder<ServerCommandSource> buildLeaveGameCommand() {
        return CommandManager.literal("leaveGame")
            .then(CommandManager.argument("gameUUID", StringArgumentType.string())
                .executes(context -> {
                    String gameIdStr = StringArgumentType.getString(context, "gameUUID");

                    UUID gameUUID = UUID.fromString(gameIdStr);
                    sendLeaveGamePacketToServer(gameUUID);

                    return 1;
                }));
    }OLD*/

    private static LiteralArgumentBuilder<ServerCommandSource> buildLeaveGameCommand() {
        return CommandManager.literal("leaveGame")
            .executes(context -> {
                sendLeaveGamePacketToServer();

                return 1;
            });
    }


    private static void sendJoinGamePacketToServer(UUID gameUUID) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeUuid(gameUUID);

        ClientPlayNetworking.send(ModMessages.MAP_CONTROLLER_SUBSCRIBE_PLAYER, buf);
    }

    private static void sendInvitePlayerGamePacketToServer(UUID invitedPlayerUUID, UUID gameUUID) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeUuid(invitedPlayerUUID);
        buf.writeUuid(gameUUID);

        ClientPlayNetworking.send(ModMessages.INVITE_PLAYER, buf);
    }

    /*private static void sendLeaveGamePacketToServer(UUID gameUUID) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeUuid(gameUUID);

        ClientPlayNetworking.send(ModMessages.MAP_CONTROLLER_UNSUBSCRIBE_PLAYER, buf);
    }OLd*/

    private static void sendLeaveGamePacketToServer() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        ClientPlayNetworking.send(ModMessages.MAP_CONTROLLER_UNSUBSCRIBE_PLAYER, buf);
    }

    public static String getPrefix() {
        return PREFIX;
    }
}
