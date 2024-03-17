package com.aureskull.zmcmod.networking.packet;

import com.aureskull.zmcmod.block.entity.SmallZombieDoorwayBlockEntity;
import com.aureskull.zmcmod.block.entity.ZombieSpawnerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RemoveLinkS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        BlockPos blockPos = buf.readBlockPos();

        client.execute(() -> {
            if (client.world != null) {
                BlockEntity blockEntity = client.world.getBlockEntity(blockPos);

                if (blockEntity instanceof SmallZombieDoorwayBlockEntity) {
                    SmallZombieDoorwayBlockEntity doorwayEntity = (SmallZombieDoorwayBlockEntity) blockEntity;
                    doorwayEntity.setLinkedSpawner(null);

                } else if (blockEntity instanceof ZombieSpawnerBlockEntity) {
                    ZombieSpawnerBlockEntity spawnerEntity = (ZombieSpawnerBlockEntity) blockEntity;
                    spawnerEntity.setLinkedDoorway(null);
                }
            }
        });
    }
}
