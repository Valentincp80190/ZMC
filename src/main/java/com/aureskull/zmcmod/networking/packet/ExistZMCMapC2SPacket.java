package com.aureskull.zmcmod.networking.packet;

import com.aureskull.zmcmod.networking.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class ExistZMCMapC2SPacket {
    private static boolean existZMCMapOnPlayerWorld = false;

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        // Vérifiez si une map ZMC existe pour le monde du joueur
        ServerWorld world = player.getServerWorld();
        existZMCMapOnPlayerWorld = checkZMCMapExistence(world);

        // Envoyez une réponse au client pour indiquer s'il est autorisé à ouvrir le GUI
        ModMessages.sendExistZMCMapCheckerResponse(player, existZMCMapOnPlayerWorld);
    }

    private static boolean checkZMCMapExistence(ServerWorld world) {
        // Implémentez votre logique pour vérifier si une map ZMC existe dans le monde donné
        // Vous pouvez vérifier l'existence du fichier JSON ou tout autre moyen de stockage que vous utilisez pour les maps ZMC
        return false;
    }
}
