package com.aureskull.zmcmod.networking.packet.zonecontroller;

import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ZoneControllerUpdateGreenC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        float newGreen = buf.readFloat();
        BlockPos blockPos = buf.readBlockPos();

        server.execute(() -> {
            ZoneControllerBlockEntity blockEntity = (ZoneControllerBlockEntity) player.getWorld().getBlockEntity(blockPos);

            if (blockEntity != null) {
                blockEntity.green = newGreen;
                blockEntity.markDirty();
            }
        });
    }
}