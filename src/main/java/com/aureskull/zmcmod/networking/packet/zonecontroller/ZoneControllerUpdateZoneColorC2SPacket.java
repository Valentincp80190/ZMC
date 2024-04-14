package com.aureskull.zmcmod.networking.packet.zonecontroller;

import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ZoneControllerUpdateZoneColorC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        BlockPos blockPos = buf.readBlockPos();
        float newColor = buf.readFloat();
        String colorVariable = buf.readString();

        server.execute(() -> {
            ZoneControllerBlockEntity blockEntity = (ZoneControllerBlockEntity) player.getWorld().getBlockEntity(blockPos);

            if (blockEntity != null) {
                switch (colorVariable){
                    case("red"):
                        blockEntity.setRed(newColor);
                        break;
                    case("green"):
                        blockEntity.setGreen(newColor);
                        break;
                    case("blue"):
                        blockEntity.setBlue(newColor);
                        break;
                }
                blockEntity.markDirty();
            }
        });
    }
}