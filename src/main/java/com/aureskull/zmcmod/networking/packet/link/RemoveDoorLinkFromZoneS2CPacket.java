package com.aureskull.zmcmod.networking.packet.link;

import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class RemoveDoorLinkFromZoneS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        BlockPos zonePos = buf.readBlockPos();
        BlockPos doorPos = buf.readBlockPos();

        client.execute(() -> {
            if (client.world != null) {
                BlockEntity zone = client.world.getBlockEntity(zonePos);

                if (zone instanceof ZoneControllerBlockEntity zoneBE) {
                    //Don't check if door exist because in case we destroy the block it doesn't exist in the world in this state
                    zoneBE.removeLinkedBlock(doorPos, DoorBlockEntity.class);
                }
            }
        });
    }
}
