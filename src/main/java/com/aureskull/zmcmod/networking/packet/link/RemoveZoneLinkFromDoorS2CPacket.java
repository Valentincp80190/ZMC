package com.aureskull.zmcmod.networking.packet.link;

import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class RemoveZoneLinkFromDoorS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        BlockPos zonePos = buf.readBlockPos();
        BlockPos doorPos = buf.readBlockPos();

        client.execute(() -> {
            if (client.world != null) {
                BlockEntity door = client.world.getBlockEntity(doorPos);

                if (door instanceof DoorBlockEntity doorBE) {
                    //Don't check if doorway exist because in case we destroy the block it doesn't exist in the world in this state
                    doorBE.removeLinkedBlock(zonePos, ZoneControllerBlockEntity.class);
                }
            }
        });
    }
}
