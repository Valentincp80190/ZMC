package com.aureskull.zmcmod.networking.packet.zonecontroller;

import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ZoneControllerUpdatePosC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        BlockPos blockPos = buf.readBlockPos();
        BlockPos newPos = buf.readBlockPos();
        String posVariable = buf.readString();

        server.execute(() -> {
            ZoneControllerBlockEntity blockEntity = (ZoneControllerBlockEntity) player.getWorld().getBlockEntity(blockPos);

            if (blockEntity != null) {
                if(new String(posVariable).equals("posA")){
                    blockEntity.posA = newPos;
                }else{
                    blockEntity.posB = newPos;
                }
                blockEntity.markDirty();
            }
        });
    }
}