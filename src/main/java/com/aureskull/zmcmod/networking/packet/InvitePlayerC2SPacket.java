package com.aureskull.zmcmod.networking.packet;

import com.aureskull.zmcmod.management.GamesManager;
import com.aureskull.zmcmod.util.ChatMessages;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class InvitePlayerC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        UUID invitedPlayerUUID = buf.readUuid();
        UUID gameUUID = buf.readUuid();

        server.execute(() -> {
            //if(invitedPlayerUUID == player.getUuid())
            //    return;

            PlayerEntity invitedPlayer = server.getPlayerManager().getPlayer(invitedPlayerUUID);
            if(invitedPlayer == null){
                Text message = Text.literal("Player not found.")
                        .formatted(Formatting.DARK_RED);
                player.sendMessage(ChatMessages.getPrefix().append(message));
            }

            GamesManager.GameInfo gameInfo = GamesManager.getInstance().getGame(gameUUID);

            if(gameInfo != null){
                ChatMessages.sendGameInviteMessage(player, ((ServerPlayerEntity) invitedPlayer), gameUUID);

                Text message = Text.literal("Invitation sent to " + invitedPlayer.getName().getString() + ".")
                        .formatted(Formatting.DARK_GREEN);
                player.sendMessage(ChatMessages.getPrefix().append(message));
            }else{
                Text message = Text.literal("Game not found.")
                        .formatted(Formatting.DARK_RED);
                player.sendMessage(ChatMessages.getPrefix().append(message));

                //PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                //playerData.gameUUID = null;
            }
        });
    }
}
