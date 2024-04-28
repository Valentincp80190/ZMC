package com.aureskull.zmcmod.networking.packet.link;

import com.aureskull.zmcmod.block.entity.MapControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.block.entity.ZombieSpawnerBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class RemoveLinkS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        BlockPos blockPos = buf.readBlockPos();

        client.execute(() -> {
            if (client.world != null) {
                BlockEntity blockEntity = client.world.getBlockEntity(blockPos);

                if (blockEntity instanceof SmallZombieWindowBlockEntity doorwayEntity) {
                    doorwayEntity.setLinkedBlock(null, ZombieSpawnerBlockEntity.class);

                } else if (blockEntity instanceof ZombieSpawnerBlockEntity spawnerEntity) {
                    spawnerEntity.setLinkedBlock(null, SmallZombieWindowBlockEntity.class);

                } else if (blockEntity instanceof MapControllerBlockEntity mapControllerEntity) {
                    mapControllerEntity.setLinkedBlock(null, ZoneControllerBlockEntity.class);

                } else if (blockEntity instanceof ZoneControllerBlockEntity zoneControllerEntity) {
                    zoneControllerEntity.setLinkedBlock(null, MapControllerBlockEntity.class);
                }
            }
        });
    }
}
