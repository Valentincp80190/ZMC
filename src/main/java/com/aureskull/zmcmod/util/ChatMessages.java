package com.aureskull.zmcmod.util;

import com.aureskull.zmcmod.event.ModCommands;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class ChatMessages {
    public static MutableText getPrefix() {
        MutableText prefix = Text.literal("[")
                .formatted(Formatting.DARK_GRAY)
                .append(Text.literal("Zombie")
                        .formatted(Formatting.DARK_RED))
                .append(Text.literal("MC")
                        .formatted(Formatting.DARK_GREEN))
                .append(Text.literal("]")
                        .formatted(Formatting.DARK_GRAY))
                .append(Text.literal(" → ")
                        .formatted(Formatting.WHITE));

        return prefix;
    }

    public static void sendGameInviteMessage(ServerPlayerEntity playerSender, ServerPlayerEntity playerReceiver, UUID gameUUID) {
        MutableText inviteMessage = Text.literal(playerSender.getName().getString() + " invite you to join a game. Click ")
                .formatted(Formatting.WHITE);

        MutableText clickableText = Text.literal("[HERE]")
                .formatted(Formatting.BOLD, Formatting.DARK_GREEN)
                .styled(style -> style.withClickEvent(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + ModCommands.getPrefix() + " joinGame " + gameUUID.toString())
                ));

        inviteMessage.append(clickableText).append(" to join the game.").formatted(Formatting.WHITE);

        playerReceiver.sendMessage(ChatMessages.getPrefix().append(inviteMessage), false);
    }

    public static void sendPlayerNotSubscribedToGameMessage(ServerPlayerEntity player) {
        MutableText message = Text.literal("You are not registered for this game.")
                .formatted(Formatting.DARK_RED);

        player.sendMessage(ChatMessages.getPrefix().append(message), false);
    }

    public static void sendGameSubscriptionConfirmationMessage(ServerPlayerEntity player, UUID gameUUID, String mapName) {
        MutableText message = Text.literal("You've successfully subscribed to '" + (mapName.isEmpty() ? "Unnamed Map" : mapName) + "'. Please wait until the game starts. Click ")
                .formatted(Formatting.DARK_GREEN);

        MutableText clickableText = Text.literal("[HERE]")
                .formatted(Formatting.BOLD, Formatting.DARK_RED)
                .styled(style -> style.withClickEvent(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + ModCommands.getPrefix() + " leaveGame " + gameUUID.toString())
                        //.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Leave the game").formatted(Formatting.RED)))
                ));

        message.append(clickableText).append(" if you wish to withdraw from the game.").formatted(Formatting.DARK_GREEN);

        player.sendMessage(ChatMessages.getPrefix().append(message), false);
    }

    public static void sendGameUnsubscriptionConfirmationMessage(ServerPlayerEntity player) {
        Text message = Text.literal("You have withdrawn from the game.").formatted(Formatting.DARK_GREEN);

        player.sendMessage(ChatMessages.getPrefix().append(message), false);
    }

    public static void sendAlreadyInGameMessage(ServerPlayerEntity player) {
        Text message = Text.literal("You're already registered for this game. Please await the game's commencement. ")
                .formatted(Formatting.WHITE)
                .append(Text.literal("Click "))
                .append(Text.literal("[HERE]")
                        .formatted(Formatting.DARK_RED)
                        .styled(style -> style
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + ModCommands.getPrefix() + " leaveGame"))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Withdraw from the game").formatted(Formatting.DARK_RED)))
                        ))
                .append(Text.literal(" if you wish to withdraw.")
                        .formatted(Formatting.WHITE));

        player.sendMessage(ChatMessages.getPrefix().append(message), false);
    }
}
