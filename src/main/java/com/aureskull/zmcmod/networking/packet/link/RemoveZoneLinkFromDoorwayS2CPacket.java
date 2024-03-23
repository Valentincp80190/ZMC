package com.aureskull.zmcmod.networking.packet.link;

import com.aureskull.zmcmod.block.entity.SmallZombieDoorwayBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class RemoveZoneLinkFromDoorwayS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        BlockPos doorwayPos = buf.readBlockPos();

        client.execute(() -> {
            if (client.world != null) {
                BlockEntity doorway = client.world.getBlockEntity(doorwayPos);

                if (doorway instanceof SmallZombieDoorwayBlockEntity) {
                    //Don't check if doorway exist because in case we destroy the block it doesn't exist in the world in this state
                    ((SmallZombieDoorwayBlockEntity) doorway).setLinkedBlock(null, ZoneControllerBlockEntity.class);
                }
            }
        });
    }
}
