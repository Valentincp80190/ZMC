package com.aureskull.zmcmod.networking.packet.player;

import com.aureskull.zmcmod.client.overlay.RoundOverlay;
import com.aureskull.zmcmod.util.PlayerData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class PlayerUpdateRoundHUDS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender){
        int newRound = buf.readInt();

        client.execute(() -> {
            RoundOverlay.updateRoundNumber(newRound);
            PlayerData.displayHUD = newRound > 0;
        });
    }
}